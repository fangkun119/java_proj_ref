[TOC]

# Spring Boot框架原理

## 01 零配置原理

### (1) 老式Tomcat部署War包时常用的三个配置

> 1. web.xml
>
>     ContextLoaderListener: 监听servlet启动、销毁、初始化IOC完成依赖注入
>
>     DispatcherServelet：接收tomcat解析后的http请求、匹配controller做业务处理
>
> 2. springmvc.xml
>
>     component-scan：扫描、只扫描@Controller
>
>     事务解析器、JSON转换、国际化、编码……
>
> 3. applicationContext.xml
>
>     component-scan：扫描各类Bean、DAO等，各个`<bean>`标签等

### (2) 用Spring MVC模拟实现Spring Boot

#### (a) 方法

官方文档[Spring Web MVC → DispatcherServlet](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-servlet)中列出以下代码

> ~~~java
> public class MyWebApplicationInitializer implements WebApplicationInitializer {
> 	@Override
> 	public void onStartup(ServletContext servletContext) {
> 		// Load Spring web application configuration
> 		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
> 		context.register(AppConfig.class);
> 		context.refresh();
> 
> 		// 创建和注册Dispatch Servlet的步骤被移到了步骤4的代码中
> 		// DispatcherServlet servlet = new DispatcherServlet(context);
> 		// ServletRegistration.Dynamic registration = servletContext.addServlet("app", servlet);
> 		// registration.setLoadOnStartup(1);
> 		// registration.addMapping("/app/*");
> 	}
> }
> ~~~

在上述代码基础上进行改动，可以用Spring MVC模拟Spring Boot，实现了一个零配置的Web Application

#### (b) 代码

##### 主类以及Controller

> 主类
>
> ~~~java
> import org.springframework.web.servlet.DispatcherServlet;
> import javax.servlet.ServletRegistration;
> 
> @ComponentScan("填入要扫描的包的包路径")
> public class Demo {
> 	public static void main(String[] args) {
>         // 需要执行的操作
> 		// 1. 应用开始启动
> 		// 2. IOC容器初始化和依赖注入，包括将DispatchServlet加载到IOC容器中
> 		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
> 		context.register(Demo.class);
> 		context.refresh();
> 		// 2. 创建内置Tomcat：见下面的步骤(2)
> 		// 3. DispatcherServlet加载到IOC容器、同时加载到Tomcat上下文中：见下面的步骤(3)        
>         // 4. 启动Tomcat的Web服务
>         WebServerFactory serverFactory = context.getBean(WebServerFactory.class);
>         serverFactory.createAndStartServer();
> 	}   
> }
> ~~~
>
> Controller
>
> ~~~java
> @RestController
> public class HelloController {
>    	@RequestMapping("/test")
>    	public String test() {
>    		return "hello world";
>    	}
> }
> ~~~
>
> @Confituration类：配置DispatcherServlet Bean，以及用于启动Tomcat的Factory Bean
>
> ~~~java
> @Configuration
> public class DemoConfig {
>    	@Bean
>    	public DispatcherServlet dispatcherServlet() {
>    		return new DispatcherServlet(context);
> 	}
> 
> 	@Bean
> 	public WebServerFactory webServerFactory() {
> 		return new TomcatFactory();
> 	}
> }
> ~~~
>
> 使用的依赖包括
>
> * [spring-webmvc 5.2.7.RELEASE](https://mvnrepository.com/artifact/org.springframework/spring-webmvc/5.2.7.RELEASE)
> * `org.apache.tomcat Tomcat8.0 8.0`

##### 内置Tomcat

> 接口：WebServerFactory
>
> ~~~java
> public interface WebServerFactory {
> 	void createAndStartServer() throws Exception;
> }
> ~~~
>
> 接口实现：TomcatFactory
>
> ~~~java
> import org.apache.catalina.startup.Tomcat;
> 
> @Component
> public class TomcatFactory implements WebServerFactory {
> 	@Override
> 	public void createAndStartServer() throws Exception {
> 		Tomcat tomcat = new Tomcat();
> 		tomcat.setPort(8080);
> 		tomcat.addwebapp("/", "./docBase/"); // 完成监听
> 		tomcat.start();
> 		tomcat.getServer().await();
> 	}
> }
> ~~~

##### 把DispatchServlet加载到Tomcat容器中

> 实现ApplicationContextAware接口，以便让这个类有能力够通过Spring Context (ApplicationContext) 获得DispatcherServlet Bean
>
> ~~~java
> // ServletInit.java
> import org.springframework.beans.BeansException;
> import org.springframework.context.ApplicationContext;
> import org.springframework.context.ApplicationContextAware;
> import org.springframework.stereotype.Component;
> import org.springframework.web.servlet.DispatcherServlet;
> 
> @Component
> public class ServletInit implements ApplicationContextAware {
> 	// IOC Context
> 	private static ApplicationContext context;
> 
> 	// 在Spring IOC启动时，获得Spring的IOC Context
> 	@Override
> 	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
> 		// Spring IOC启动时
> 		// * 会扫描ApplicationContextAware的实现类
> 		// * 调用实现类的setApplicationContext方法，并传入IOC容器的上下文
> 		this.context = applicationContext;
> 	}
> 
>    	// 从IOC Context中返回DispatchServlet Bean
> 	public static DispatcherServlet getServlet() {
>         // DispatcherServlet Bean在扫描到DemoConfig类时被加载到容器中
> 		return context.getBean(DispatcherServlet.class);
> 	}        
> }
> ~~~
>
> 实现WebApplicationInitializer接口，以便在Tomcat启动时将上面从Spring Context中得到的DispatcherServlet交给Tomcat Servlet Context中
>
> ~~~java
> // WebAppInit.java
> import org.springframework.web.WebApplicationInitializer;
> import javax.servlet.ServletContext;
> import javax.servlet.ServletException;
> import javax.servlet.ServletRegistration;
> 
> public class WebAppInit extends ServletInit implements WebApplicationInitializer {
> 	@Override
> 	public void onStartup(ServletConext servletContext) throws ServletException {
> 		// 将Spring Context中的DispatchServlet注册查到Tomcat的Servlet Context中
> 		ServletRegistration.Dynamic dynamic
>             = servletContext.addServlet(
>             	"app", 
>             	getServlet() //基类的方法：返回Spring Context中的DispatchServlet
>             );
> 		dynamic.setLoadOnStartup(1);
> 		dynamic.addMapping("/"); 
> 	}
> }
> ~~~

#### (c) 代码说明

1 老式应用中的三处配置如何被替代

> web.xml：被TomcatFactory类所替代
>
> springmvc.xml：被WebAppInit和它的基类ServletInit所替代
>
> applicationContext.xml：被@Configuration类、component-scan以及@Component替代

2 `步骤(3)`中的onStartup方法如何被触发，从而将Spring Context中的DispatchServlet注册查到Tomcat的Servlet Context中

> 如`DispatcherServlet`的[官方文档](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-servlet)中描述，实现`org.springframework.web.WebApplicationInitializer`接口的类，会被Servlet容器自动扫描并执行。而Tomcat能够调用到Spring（非Tomcat）的接口，是因为使用了SPI技术。

## 02 Tomcat启动：SPI在Spring Boot代码中的应用

> 如上一节末尾，借助SPI可以让Tomcat调用Spring的接口
>
> * Tomcat调用的Servlet接口：javax.servlet.ServletContainerInitializer
> * Spring提供的接口：org.springframework.web.WebApplicationInitializer
>
> 其实现原理如下

### (1) SPI服务发现机制（Service Provider Interface）

SPI机制要求如下

> 1. 配置文件：
>
>     文件名是`META-INF/services/${interface_path}`，其中`${interface_path}`是接口的全路径，文件内容是实现类的全路径
>
> 2. 自定义的类加载器：
>
>     必须加载该接口以及实现

### (2) 用SPI打破JVM的双亲委派

> 双亲委派：通过把加载操作委托给上层加载器、来保证类型加载安全
>
> 以JDBC为例
>
> 1. 应用场景
>
>     不希望硬编码形如`Class.forName("com.mysql.jdbc.Driver")`
>
>     通过`DriverManager.getConnection("jdbc:127.0.0.1:6036")`来实现，这个方法会根据SPI规范扫描`META-INF/services`查找`MySQL Driver`的实现类进行加载
>
> 2. 双亲委派
>
>     `java.jdbc.Driver`和`DriverManager`位于`rt.jar`，由最顶层BootStrapClassLoader负责
>
>     根据加载规范、`DriverManager`的依赖类也由同一个加载类负责，因此也是BootStrapClassLoader
>
> 3. 双亲委派的打破
>
>     而通过SPI扫描到的`com.mysql.jdbc.Driver`是JDBC的类，BootStrapClassLoader无法加载，因此被一路下放给上下文加载器（包括自定义加载器），从而打破双亲委派

### (3) Spring Boot使用SPI让Tomcat调用Spring的接口

Servlet规范

> 扫描到ServletContainerInitializer接口的实现时，必须调用它的onStartUp方法
>
> Tomcat同样遵守该规范

Spring如何让Tomcat能够调用不属于它的WebApplicationInitializer类

> (1) 通过SPI让Tomcat调用的Spring的SpringServletContainerInitializer
>
> * SpringServletContainerInitializer实现了ServletContainerInitializer接口
> * 通过SPI配置为ServletContainerInitializer指定其实现为SpringServletContainerInitializer（可以在spring-web-5.2.7.RELEASE.jar的META-INF/services/中找到SPI配置）
>
> (2) 让SpringServletContainerInitializer调用WebApplicationInitializer的方法
>
> * SpringServletContainerInitializer被`@HandlersTypes({WebApplicationInitializer.class})`注解，是类似CGLIB、JAVASIST等的字节码技术
>
> * 在这个例子中，`@HandlersTypes`它会通过字节码找到所有WebApplicationInitializer的实现类，并注入到SpringServletContainerInitializer的onStartup方法的`Set<Class<?>> webAppInitializerClasses`参数中
>
>     ~~~java
>     @HandlesTypes({WebApplicationInitializer.class})
>     public class SpringServletContainerInitializer implements ServletContainerInitializer {
>         ...
>         public void onStartup(@Nullable Set<Class<?>> webAppInitializerClasses, ServletContext servletContext) {
>             ...
>         }
>     }
>     ~~~
>
> * SpringServletContainerInitializer的onStartUp会遍历所有注入的实现类，调用他们的onStartUp方法

## 03 Spring Boot自动配置Spring MVC的源代码

### (1) Demo代码

主类

> ~~~java
> @SpringBootConfiguration
> @ComponentScan("填入要扫描的包路径")
> public class Demo {
>    	public static void main(String[] args) throws Exception {
>    		SpringApplication.run(Demo.class, args);
>    	}
> }
> ~~~

Controller

> ~~~java
> @RestController
> public class HelloController {
>    	@RequestMapping("/test")
>    	public String test() {
>    		return "hello world";
>    	}
> }
> ~~~

依赖

> | groupId                  | artifactId                | Version       |
> | ------------------------ | ------------------------- | ------------- |
> | org.springframework      | spring-webmvc:            | 5.2.7-RELEASE |
> | org.apache.tomcat        | Tomcat8.0                 | 8.0           |
> | org.springframework.boot | spring-boot               | 2.3.1-RELEASE |
> | org.springframework.boot | spring-boot-autoconfigure | 2.3.1-RELEASE |
>
> spring-boot-autoconfigure用于自动装配

### (2) Spring Boot启动源代码

#### (a) 如何启动Tomcat

→ `SpringApplication.run(...)` 

> ~~~java
> public static ConfigurableApplicationContext run(Class<?>[] primarySource, String[] args) {
> 	return new SpringApplication(primarySources).run(args);
> }
> ~~~

→ `SpringApplication.run(...)`

> ~~~java
> ConfigurableApplicationContext context = null;
> ...
> try {
>    	...
> 	// 创建IOC容器，根据参数的不同，创建不同的容器（SERVLET，REACTIVE）
> 	context = createApplicationContext(); 
>    	...
> 	// 依赖注入
> 	prepareContext(context, environment, listeners, applicationArguments, printedBanner); 
>     	// 下面步骤中会启动Tomcat
>    	// * 通过调用ServletWebServerApplicationContext的onRefresh方法
>    	refreshContext(context); 
> }
> ~~~
>
> → ServletWebServerApplicationContext.onRefresh
>
> → ServletWebServerApplicationContext.createWebServer
>
> ~~~java
> private void createWebServer() {
>    	...
> 	ServletWebServerFactory factory = getWebServerFactory();
> 	this.webServer = factory.getWebServer(getSelfInitializer());
> }
> ~~~
>
> > → TomcatServletWebServerFactory.getWebServer
> >
> > ~~~java
> > @Override
> > public WebServer getWebServer(ServletContextInitializer... initializers) {
> > 	...
> > 	Tomcat tomcat = new Tomcat();
> > 	...
> > 	return getTomcatWebServer(tomcat);
> > }
> > 
> > protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
> >    	return new TomcatWebServer(tomcat, getPort() >= 0, getShudown());
> > }
> > ~~~
> >
> > > → TomcatWebServer
> > >
> > > ~~~java
> > > public TomcatWebServer(Tomcat tomcat, boolean autoStart, Shutdown shutdown) {
> > >    	...
> > > 	initialize();
> > > }
> > > 
> > > private void initialize throws WebServerException {
> > > 	...
> > > 	this.tomcat.start(); //启动Tomcat
> > > 	...
> > > 	startDaemonAwaitThread(); 
> > > 	...
> > > }
> > > ~~~

#### (b) 如何把DispatchServlet设置给Tomcat

入口

> 在spring-boot-autoconfigure jar包的META-INF目录中的springfactories中可以看到：
>
> ~~~txt
> org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration, \
> ~~~

代码：注册DispatcherServlet到IOC容器、并让Tomcat在启动时调用

> → DispatcherServletAutoConfiguration (上面模拟实现中的DemoConfig类作用于此类似)
>
> ~~~java
> public class DispatcherServletAutoConfiguration {
> 	...
> 
> 	@Configuration(proxBeanMethods = false)
> 	@Conditional(DefaultDispatcherServletCondition.class)
> 	@ConditionalOnClass(ServletRegistration.class)
> 	@EnableConfigurationProperties(WebMvcProperties.class)
> 	public static class DispatcherServletConfiguration {
> 
>         // 将DispatcherServlet加载到IOC容器中
> 		@Bean(name = DEFAULT_DISPATCHER_SERVLET_BEAN_NAME) 
> 		publci DispatcherServlet dispatcherServlet(WebMvcProperties webMvcProperties) {
> 			DispatcherServlet dispatcherServlet = new DispatcherServlet();
> 			...
> 			return dispatcherServlet;
>         }
> 		...
>     }
>     ...
> 	
> 	@Configuration(proxBeanMethods = false)
> 	@Conditional(DefaultDispatcherServletCondition.class)
> 	@ConditionalOnClass(ServletRegistration.class)
> 	@EnableConfigurationProperties(WebMvcProperties.class)
>     @Import(DispatcherServletConfiguration.class)
> 	protected static class DispatcherServletRegistrationConfiguration {
> 
> 		@Bean(name = DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME)
> 		@ConditionalOnBean(value = DispatcherServlet.class, name = DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
> 		public DispatcherServletRegistrationBean dispatcherServletRegistration(DispatcherServlet dispatcherServlet,
> 				WebMvcProperties webMvcProperties, ObjectProvider<MultipartConfigElement> multipartConfig) {
> 			DispatcherServletRegistrationBean registration = new ...
> 			...
> 			// 与让Tomcat加载IOC容器中的DispatchServlet
> 			registration.setLoadOnStartup(webMvcProperties.getServlet().getLoadOnStartup());
> 			...
> 			return registration;
>         }
>     }
> 	...
> }
> ~~~
>
> > → DispatcherServletRegistrationBean
> >
> > ~~~java
> > public class DispatcherServletRegistrationBean extends ServletRegistrationBean<DispatcherServlet> implements DispatcherServletPath {
> >     ...
> > }
> > ~~~
> >
> > → ServletRegistrationBean
> >
> > <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/080_spring_concept/001_spring_080_internal_servletregistrationbean_class_diag.jpg" width="600" /></div>
> >
> > ~~~java
> > class DynamicRegistrationBean extends RegistrationBean implements Dynamic {
> > 	...
> > 	// ServletContextInitializer接口的方法，负责将Servlet添加到Servlet容器中
> >     @Override
> > 	protected ServletRegistration.Dynamic addRegistration(String description, ServletContext servletContext) {
> >         String name = getServletname();
> >         return servletContext.addServlet(name, this.servlet);
> >     }
> >     ...
> > }
> > ~~~
> >
> > →ServletContextInitializer
> >
> > ~~~java
> > @FunctionalInterface
> > public interface ServletContextInitializer {
> >     void onStartup(ServletContext servletContext) throws ServletException;
> > }
> > ~~~
> >
> > * 之前模拟实现小节中，使用的是ServletContainerInitializer接口，这个来自Spring接口被Tomcat以SPI的方法调用
> > * 不仅如下，Tomcat还会通过调用ServletContainerInitializer来调用到ServletContextInitializer接口
> >
> > 调用原理如下：
> >
> > (1) 通过StandardContext类可以设置Initailizer
> >
> > ~~~java
> > @SuppressWarnings("deprecation")
> > public class StandardContext extends ContainerBase implements Context, NotificationEmitter {
> > 	...
> > 	@Override
> > 	public void addServletContainerInitializer(
> > 			ServletContainerInitializer sci, Set<Class<?>> classes) {
> > 		// initializers是一个全局变量
> > 		// (1) Tomcat启动时会遍历其中所有的intializer并调用onStartup方法
> > 		// (2) 这个过程通过SPI来实现
> > 		initializers.put(sci, classes);
> >     }
> > }
> > ~~~
> >
> > (2) Tomcat在启动时，会遍历initializers并调用元素的onStartup方法
> >
> > ~~~java
> > package org.apache.catalina.util
> > 
> > class LifecycleBean {
> > 	...
> > 
> > 	@Override
> > 	protected synchronized void startInternal() throws LifecycleException {
> >     	...
> > 		for (Map.Entry<ServletContainerInitializer, Set<Class<?>>> entry: initializers.entrySet()) {
> > 			try {
> > 				entry.getKey().onStartup(entry.getValue(), getServletContext());
> > 			} catch (ServletException e) {
> > 				...
> > 			}
> > 		}
> > 	}
> > 	...
> > }    
> > ~~~
> >
> > (3) 要通过SPI做的事情，就是让Tomcat在访问intializers时，能够找到Spring的initializers实现类
> >
> > (4) 具体如何调用到上面Spring的ServletContextInitializer接口
> >
> > → ServletWebServerApplicationContext 
> >
> > ~~~java
> > class ServletWebServerApplicationContext {
> >     ...
> > 
> > 	private void createWebServer() {
> > 		...
> > 		// 刚启动、Tomcat上下文还为null的时候
> > 		if (webServer == null && servletContext == null) {
> > 			...
> > 			// 
> > 			this.webServer = factory.getWebServer(getSelfInitializer() /*只设置了一个lambda表达式*/);
> > 			...
> >         }
> > 		// Spring Boot打War包部署时
> >         else if (serveltContext != null) {
> >             ...
> >         }
> > 	}
> > 
> > 	private org.springframework.boot.web.servlet.ServletContextInitializer getSelfInitializer() {
> > 		return this::selfInitialize;
> > 	}
> > 
> >     private void selfInitialize(ServletContext servletContext) throws ServletException {
> > 		...
> > 		for (ServletContextInitializer beans : getServletContextInitializerBeans()) {
> > 			beans.onStartup(servletContext);
> > 		}
> > 	}
> > }
> > ~~~
> >
> > > → ServletWebServerFactory
> > >
> > > ~~~java
> > > @FunctionalInterface
> > > public interface ServletWebServerFactory {
> > > 	WebServer getWebServer(ServletContextInitializer... initializers);
> > > }
> > > ~~~
> > >
> > > → TomcatServletWebServerFactory
> > >
> > > ~~~java
> > > class TomcatServletWebServerFactory implements ServletWebServerFactory {
> > > 	...
> > > 	@Override
> > > 	public WebServer getWebServer(ServletContextInitializer... initializers) {
> > >         ...
> > > 		prepareContext(tomcat.getHost(), initializers);
> > > 		return getTomcatWebServer(tomcat);
> > >     }
> > > 
> > >     protected void prepareContext(Host host, ServletContextInitializer[] initializers) {
> > >         ...
> > > 		// TomcatEmbeddedContext 实现了上面的 StandardContext
> > > 		TomcatEmbeddedContext context = new TomcatEmbeddedContext();
> > > 		...
> > > 		// 对initializers做合并处理
> > > 		ServletContextInitializer[] initializersToUse = mergeInitializers(initializers);
> > >         ...
> > > 		// 将intializers配置到context中
> > > 		configureContext(context, initializersToUse);
> > > 		postProcessContext(context);
> > >     }
> > > 	
> > > 	protected void configureContext(Context context, ServletContextInitializer[] intitializers) {
> > >         // TomcatStarter是ServletContainerInitializer的子类
> > > 		// 而之前模拟代码中的ServletContextInitializer是要调用的类，如何让它被调用？
> > > 		// (1) TomcatStarter是ServletContainerInitializer的子类
> > > 		// (2) ServletContainerInitializer会在Tomcat启动时被调用
> > > 		// (3) 而目标类ServletContextInitializer则被包裹在ServletContainerInitializer中，因此也能一同被调用
> > > 		TomcatStarter starter = new TomcatStarter(initializers);
> > >         ...
> > > 		
> > >         // 类型是TomcatEmbeddedContext extends StandardContext
> > > 		// 会把ServletContainerInitializer放入initializers变量中
> > > 		// 具体见本小节顶部的介绍StandardContext的代码
> > > 		context.addServletContainerInitializer(starter, NO_CLASSES);
> > >     }	
> > > }
> > > 
> > > ~~~
> > >
> > > > → TomcatEmbeddedContext
> > > >
> > > > ~~~java
> > > > class TomcatEmbeddedContext extends StandardContext {
> > > >     ...
> > > > }
> > > > ~~~
> > > >
> > > > TomcatEmbeddedContext阶乘了本小节顶部StandardContext，它是Tomcat中的容器，把ServletContainerInitializer放入initializers变量中
> > > >
> > > > → TomcatStarter
> > > >
> > > > ~~~java
> > > > class TomcatStarter implements ServletContainerInitializer {
> > > >     ...
> > > > 	private final ServletContextInitializer[] initializers;
> > > > 	TomcatStarter(ServletContextInitializer[] initializers) {
> > > >         this.initializers = initializers;
> > > >     }
> > > > 
> > > >     @Override
> > > >     public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
> > > >         try {
> > > >             for (ServletContextInitializer(initializer: this.initializers)) {
> > > >                 initializer.onStartup(servletContext);
> > > >             }
> > > >         } eatch (Exception ex) {...}
> > > >         ...
> > > >     }
> > > >     
> > > > }
> > > > ~~~

综上所述：原理如下

> (1) 通过SPI让Spring的ServletContextInitializer的onStartup方法被Tomcat调用
>
> (2) 通过类型包裹等方式，利用上面
>
> * 利用Tomcat的SPI让ServletContextInitializer替代Tomcat的ServletContainerInitializer
>
> * 通过Spring的SPI让WebApplicationInitializer替代Tomcat的ServletContainerInitializer

### (3) 设计考虑

#### (a) Spring/Spring Boot通过SPI用自己的接口取代Servlet规范中的接口

原因是为屏蔽中间件Tomcat，与Tomcat解耦

> ServletContextInitializer是Spring的接口，避免业务代码依赖Tomcat的接口
>
> ServletContextInitializer的实现类都在IOC容器中、使用方便
>
> 可以向Tomcat中添加DispatcherServlet，同样可以向Tomcat添加Listener和Filter等

## 04 自动装配原理

> 自动装配的原理、就是Starter编写的原理

### (1) 使用Starter解决的问题

#### (a) 传统Jar包的问题

> 业务方需要编写代码配置Bean（@Autowired、依赖路径等）
>
> 一旦Jar包发生更新，可能会影响到业务方，需要业务方重新配置

#### (b) 使用starter作为依赖

>业务方不再需要编写代码配置Bean
>
>只需要在pom中依赖这个Jar包，然后通过@Autowired来注入这个Bean

### (2) Demo项目

> 原本在Spring MVC项目中需要配置Spring、Spring MVC、web.xml、applicationContext.xml等
>
> 但是改用Spring Boot之后，只需要引入spring-boot-starter-web，即可通过注解@SpringBootApplication来免去这些配置

Controller

> ~~~java
> @RestController
> public class HelloController {
> 	@RequestMapping("/test") 
> 	public String test() {
> 		return "hello world";
> 	}
> }
> ~~~

启动类

> ~~~java
> @SpringBootApplication
> public class DemoApplication {
> 	public static void main(String[] args) {
> 		SpringApplication.run(DemoApplication.class, args);
> 	}
> }
> ~~~

`<parent>`

> | groupId                  | artifactId                 | version       |
> | ------------------------ | -------------------------- | ------------- |
> | org.springframework.boot | spring-boot-starter-parent | 2.3.1.RELEASE |

`<dependencies>`

> | groupId                          | artifactId                | comment                 |
> | -------------------------------- | ------------------------- | ----------------------- |
> | org.springframework.boot         | spring-boot-starter-web   | 是一个starter依赖       |
> | org.springframework.boot         | spring-boot-autoconfigure |                         |
> | com.myprojdemo.springbootstarter | util-spring-boot-starter  | 准备编写的starter依赖包 |

说明

1 . 使用不同的starter引入不同的功能支持

> (1) 使用spring-boot-starter-web：spring、springmvc相关的配置都可以省去。
>
> (2) 同样、如果使用MyBatis的starter，可以省去mybatis、sqlsessionfactory等配置

2 . 依赖版本管理

> 配置在`<dependencies>`中的依赖不需要版本号，原因如下
>
> (1) 项目parent是spring-boot-starter-parent 2.3.1.RELEASE，而它上一层的parent是spring-boot-dependencies 2.3.1.RELEASE
>
> (2) 在spring-boot-dependencies 2.3.1.RELEASE中，有`<dependencyManagement>`定义了各个依赖的版本

3 . Jar包冲突避免

> 假如想使用ActiveMQ，只需再引入ActiveMQ的Starter，因为parent同属于spring-boot-starter-parent 2.3.1.RELEASE，因此最终定位到的`<dependencyManagement>`是同一个。而这些依赖的版本都已经预先进行过Jar包冲突处理，不会发生Jar包冲突

4 . Spring Boot为何要在`spring-boot-starter-*`和`spring-boot-dependencies`之间添加一层`spring-boot-starter-parent`

> 用来覆盖默认配置。例如在application.yml里面添加配置、把默认的Tomcat切换成Jetty或undertom等。在`spring-boot-starter-parent`的pom.xml中可以看到配置文件加载，作为一个中间层处理定制化的配置等。
>
> ~~~xml
> <resource>
> 	<directory>${basedir}/src/main/resources</directory>
> 	<filtering>true</filtering>
> 	<includes>
> 		<include>**/application*.yml</include>
> 		<include>**/application*.yaml</include>
> 		<include>**/application*.properties</include>
> 	</includes>
> </resource>
> ~~~

下一小节会继续修改这个项目，添加并使用一个自定义Starter

### (3) 自动配置原理：@SpringBootApplication

> 通过@SpringBootApplication注解来触发自动配置，其源代码和说明如下各个相关的注解源代码及注释如下
>
> ~~~java
> // 元注解：用来修饰自定义注解的注解
> @Target(ElementType.TYPE)			// 作用范围：类；方法；属性
> @Retention(RetentionPolicy.RUNTIME)	// 生命周期：编译期生效（例如@Override注解）；运行期生效（例如@SpringBootApplication）
> @Documented							// 开启JavaDoc注释：配合代码注释块中的@parent、@see等JavaDoc注解使用
> @Inherited							// 自定义注解可被子类继承：例如某个类使用了@SpringBootApplication注解，他们它的子类也会自动拥有该注解
> // 复合注解
> @SpringBootConfiguration
> @EnableAutoConfiguration
> @ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
> 		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
> public @interface SpringBootApplication {
> 
> 	@AliasFor(annotation = EnableAutoConfiguration.class)
> 	Class<?>[] exclude() default {};
> 
> 	@AliasFor(annotation = EnableAutoConfiguration.class)
> 	String[] excludeName() default {};
> 
> 	@AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
> 	String[] scanBasePackages() default {};
> 
> 	@AliasFor(annotation = ComponentScan.class, attribute = "basePackageClasses")
> 	Class<?>[] scanBasePackageClasses() default {};
> 
> 	@AliasFor(annotation = ComponentScan.class, attribute = "nameGenerator")
> 	Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;
> 
> 	@AliasFor(annotation = Configuration.class)
> 	boolean proxyBeanMethods() default true;
> }
> ~~~

#### (a) `@SpringBootConfiguration`

> @SpringBootConfiguration：实际上就是@Configuration
>
> ```java
> @Target(ElementType.TYPE)
> @Retention(RetentionPolicy.RUNTIME)
> @Documented
> // 复合注解
> @Configuration
> public @interface SpringBootConfiguration {
> 	@AliasFor(annotation = Configuration.class)
> 	boolean proxyBeanMethods() default true;
> }
> ```
>
> @Configuration
>
> ~~~java
> @Target(ElementType.TYPE)
> @Retention(RetentionPolicy.RUNTIME)
> @Documented
> // 复合注解
> @Component
> public @interface Configuration {
> 	@AliasFor(annotation = Component.class)
> 	String value() default "";
> 	// proxyBeanMethods值为true：
> 	// * 默认使用CGLib代理该注解修饰的类
> 	// * @Configuration修饰的类里面，调用自己的使用@Bean注解的方法，会被代理，返回注册在容器中的Bean，而不是直接调用
> 	boolean proxyBeanMethods() default true;
> }
> ~~~

#### (b) `@EnableAutoConfiguration`

##### @EnableAutoConfiguration ← @AutoConfigurationPackage

> `@EnableAutoConfiguration`
>
> ```java
> @Target(ElementType.TYPE)
> @Retention(RetentionPolicy.RUNTIME)
> @Documented
> @Inherited
> // 参考下面的代码注释
> @AutoConfigurationPackage
> // 将AutoConfigurationImportSelector的selectImports方法返回的全路径数组对应的所有类，注入到IOC容器
> @Import(AutoConfigurationImportSelector.class)  
> public @interface EnableAutoConfiguration {
>    String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";
>    Class<?>[] exclude() default {};
>    String[] excludeName() default {};
> }
> ```
>
> `@AutoConfigurationPackage`
>
> ```java
> @Target(ElementType.TYPE)
> @Retention(RetentionPolicy.RUNTIME)
> @Documented
> @Inherited
> // 导入参数类到IOC容器
> @Import(AutoConfigurationPackages.Registrar.class)	
> public @interface AutoConfigurationPackage {
>    String[] basePackages() default {};
>    Class<?>[] basePackageClasses() default {};
> }
> ```

##### @Import：Spring Boot自动装配的核心注解

> 三种用法：
> (1) 参数是普通类：将该类实例化并交给IOC容器管理
> (2) 参数是ImportBeanDefinitionRegistrar的实现类：支持手工注册Bean（例如上面的AutoConfigurationPackages.Registrar）
> (3) 参数是ImportSelector的实现类：会注册它的selectImports返回数组所表示的类到IOC容器（例如上面的AutoConfigurationImportSelector类）
>
> ~~~java
> @Target(ElementType.TYPE)
> @Retention(RetentionPolicy.RUNTIME)
> @Documented
> public @interface Import {
> 	Class<?>[] value();
> }
> ~~~

##### 辅助类：AutoConfigurationPackages和ImportSelector

> AutoConfigurationPackages
>
> ```java
> public abstract class AutoConfigurationPackages {
> 	static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {
> 		// Spring实例化Bean时使用的方法，实现这个方法可以手工注册Bean
> 		@Override
> 		public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
> 			// register的操作步骤：
> 			// 保存扫描路径、提供给spring-data-jpa等查询用（例如查找使用@Entity、@Dao等注解的类）
> 			register(
> 				registry, 
> 				// PackageImports用来获得要扫描的包：(1) 首选注解属性basePackages；（2）如果没有就使用被注解类所在包的包名
> 				new PackageImports(metadata).getPackageNames().toArray(new String[0])
> 			);
> 		}
> 		...
>     }
> }
> ```
>
> AutoConfigurationImportSelector 和 SpringFactoriesLoader 类
>
> ~~~java
> /******************************************************************************
> * AutoConfigurationImportSelector
> ******************************************************************************/
> public class AutoConfigurationImportSelector implements DeferredImportSelector, BeanClassLoaderAware,
> 		ResourceLoaderAware, BeanFactoryAware, EnvironmentAware, Ordered {
> 	...
> 
> 	@Override
> 	public String[] selectImports(AnnotationMetadata annotationMetadata) {
> 		// 判断注解是否开启
> 		if (!isEnabled(annotationMetadata)) {
> 			return NO_IMPORTS;
> 		}
> 		// 获取META-INF/spring.factories中配置的@Configruation类
> 		AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(annotationMetadata);
> 		// 转成String[]返回
> 		return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
> 	}
> 
> 	// 自动装配方法的核心
> 	protected AutoConfigurationEntry getAutoConfigurationEntry(AnnotationMetadata annotationMetadata) {
> 		// 判断注解是否开启
>         if (!isEnabled(annotationMetadata)) {
> 			return EMPTY_ENTRY;
> 		}
> 		// 获取注解属性
> 		AnnotationAttributes attributes = getAttributes(annotationMetadata);
> 		// 从META-INF/spring.factories配置文件中取到一组@Configuration类，其中包括EnableAutoConfiguration
> 		List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
> 		// 去重
> 		configurations = removeDuplicates(configurations);
> 		// 可以通过注解排除不想加载的类
> 		Set<String> exclusions = getExclusions(annotationMetadata, attributes);
> 		checkExcludedClasses(configurations, exclusions);
> 		configurations.removeAll(exclusions);
> 		// 过滤
> 		configurations = getConfigurationClassFilter().filter(configurations);
> 		// 把AutoConfigurationImportEvent绑定到监听器中，用来发布事件
> 		fireAutoConfigurationImportEvents(configurations, exclusions);
> 		// 包装并返回
> 		return new AutoConfigurationEntry(configurations, exclusions);
> 	}
> 	
> 	protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
> 		// 
> 		List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),
> 				getBeanClassLoader());
> 		Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you "
> 				+ "are using a custom packaging, make sure that file is correct.");
> 		return configurations;
> 	}
> }
> 
> /******************************************************************************
> * SpringFactoriesLoader
> ******************************************************************************/
> public final class SpringFactoriesLoader {
> 	...
> 	public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader) {
> 		...
> 		return (List)loadSpringFactories(classLoaderToUse).getOrDefault(factoryTypeName, Collections.emptyList());
> 	}
> 
> 	private static Map<String, List<String>> loadSpringFactories(ClassLoader classLoader) {
> 		...
> 		// 从配置文件META-INF/spring.factories中加载，文件内容包含
> 		// ...
> 		// # Auto Configure
> 		// org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
> 		// ...
> 		// 这些类每个都是一个@Configuration类，其中定义了各种Bean，都会被加载到IOC容器中
> 		Enumeration urls = classLoader.getResources("META-INF/spring.factories");
> 		...
> 	}
> 	...
> }
> ~~~
>
> ImportSelector
>
> ```java
> public interface ImportSelector {
> 	// 以全路径的方式批量返回要注册的类
> 	String[] selectImports(AnnotationMetadata importingClassMetadata);
> 
> 	@Nullable
> 	default Predicate<String> getExclusionFilter() {
> 		return null;
> 	}
> }
> ```

#### (c) `@ComponentScan`

> ~~~java
> package org.springframework.context.annotation;
> ...
> 
> @Retention(RetentionPolicy.RUNTIME)
> @Target(ElementType.TYPE)
> @Documented
> @Repeatable(ComponentScans.class)
> public @interface ComponentScan {
> 
> 	@AliasFor("basePackages")
> 	String[] value() default {};
> 
> 	@AliasFor("value")
> 	String[] basePackages() default {};
> 
> 	Class<?>[] basePackageClasses() default {};
> 	Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;
> 	Class<? extends ScopeMetadataResolver> scopeResolver() default AnnotationScopeMetadataResolver.class;
> 	ScopedProxyMode scopedProxy() default ScopedProxyMode.DEFAULT;
> 	String resourcePattern() default ClassPathScanningCandidateComponentProvider.DEFAULT_RESOURCE_PATTERN;
> 	boolean useDefaultFilters() default true;
> 	Filter[] includeFilters() default {};
> 	Filter[] excludeFilters() default {};
> 	boolean lazyInit() default false;
> 
> 	@Retention(RetentionPolicy.RUNTIME)
> 	@Target({})
> 	@interface Filter {
> 
> 		FilterType type() default FilterType.ANNOTATION;
> 
> 		@AliasFor("classes")
> 		Class<?>[] value() default {};
> 
> 		@AliasFor("value")
> 		Class<?>[] classes() default {};
> 
> 		String[] pattern() default {};
> 	}
> }
> ~~~

## 05 自定义Starter

### (1) Demo项目

项目分成两个Idea Module，对应的artifact如下

> `util-spring-boot-autoconfig`：编写业务逻辑，内容参照上一小节的Demo项目进行修改
>
> `utile-spring-boot-starter`：管理依赖

