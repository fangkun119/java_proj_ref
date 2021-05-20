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

官方文档[Spring Web MVC → DispatcherServlet](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-servlet)中列出以下代码

> ~~~java
> public class MyWebApplicationInitializer implements WebApplicationInitializer {
> 	@Override
> 	public void onStartup(ServletContext servletContext) {
> 		// Load Spring web application configuration
> 		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
> 		context.register(AppConfig.class);
>         context.refresh();
> 
>         // 创建和注册Dispatch Servlet的步骤被移到了步骤4的代码中
> 		// DispatcherServlet servlet = new DispatcherServlet(context);
> 		// ServletRegistration.Dynamic registration = servletContext.addServlet("app", servlet);
> 		// registration.setLoadOnStartup(1);
> 		// registration.addMapping("/app/*");
> 	}
> }
> ~~~

在上述代码基础上进行改动，可以用Spring MVC模拟Spring Boot，实现了一个零配置的Web Application

(1) 主类以及Controller

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
>     @RequestMapping("/test")
>     public String test() {
>         return "hello world";
>     }
> }
> ~~~
>
> @Confituration类：配置DispatcherServlet Bean，以及用于启动Tomcat的Factory Bean
>
> ~~~java
> @Configuration
> public class DemoConfig {
>     @Bean
>     public DispatcherServlet dispatcherServlet() {
>         return new DispatcherServlet(context);
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

(2) 内置Tomcat

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

(4) 把DispatchServlet加载到Tomcat容器中

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
>     // 从IOC Context中返回DispatchServlet Bean
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

代码说明：

1 老式应用中的三处配置如何被替代

> web.xml：被TomcatFactory类所替代
>
> springmvc.xml：被WebAppInit和它的基类ServletInit所替代
>
> applicationContext.xml：被@Configuration类、component-scan以及@Component替代

2 `步骤(3)`中的onStartup方法如何被触发，从而将Spring Context中的DispatchServlet注册查到Tomcat的Servlet Context中

> 如`DispatcherServlet`的[官方文档](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-servlet)中描述，实现`org.springframework.web.WebApplicationInitializer`接口的类，会被Servlet容器自动扫描并执行。而Tomcat能够调用到Spring（非Tomcat）的接口，是因为使用了SPI技术。

## SPI在Spring Boot代码中的应用

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

