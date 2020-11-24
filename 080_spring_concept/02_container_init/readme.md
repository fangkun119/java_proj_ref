# 02 容器初始化 

## 1 容器

### (1) 整体过程

> `xml`,  `注解`，……  (bean defination）==> 容器 （bean  info  -> create bean object）

### (2) 如何实例化bean object 

> 保证高内聚、低耦合、在不同的阶段可以增加不同的操作

(a) 创建`Bean工厂`：创建`Bean Factory`

> 如果希望动态地往bean里面填不同的内容呢？即在创建bean object之前、对bean  info进行增强？

(b) Bean工厂增强：用`BeanFactoryPostProcesser`对`Bean  Factory`进行增强来实现

(c) 创建`普通对象`：`Bean Factory`用`cos = class.getConstructor();  obj = cos.newInstance();` 创建出`普通对象`

> 但如果bean还依赖其他的bean呢（`@Autowired`，`@Value`，xml配置中的`SpEL`）、还如何处理呢？

(d) 创建`容器对象`：在`cos.newInstance()`之后，用`BeanPostProcessor`对`普通对象`进行增强

> 注：所谓的“普通对象”、“容器对象”只是为了方便理解，其实内存中是同一个对象

### (3) Environment

> 将`*.properties`、`*.yml`、`System.property`等存储的`<key, value>`注入到`bean`的属性上

## 2 Demo



## 3 代码

> 查看源代码，以`BeanFactory.java`为例，在IDEA中将项目构建好之后，`CMD + N`后搜索`BeanFactory`，打开`BeanFactory.class`，点击窗口顶部提示栏的`download sourcecode`，即可以下载并打开`BeanFactory.java`

[`BeanFactory.java`](code_sample/BeanFactory.java)

"The **root interface** for accessing a Spring bean container. further interfaces such as {@link **ListableBeanFactory**} and {@link org.springframework.beans.factory.config.**ConfigurableBeanFactory**} are available for specific purposes

> `BeanFactory`是根接口、派生（extends）出诸如`ListableBeanFactory`、`ConfigurableBeanFactory`之类的子接口

"hold a number of bean definitions, each uniquely identified by a String name" 

> 容器内装载的bean，每一个都有String类型的Name来唯一标识（从bean defination读入）

"return either an independent instance (Prototype bean)，or a single shared instance (Singleton Bean) ...  Since Spring
 * 2.0, further scopes are available ... (e.g.  "request" and "session")"

> bean的作用域：
> 
> * `Singleton`：`factory scope`内对象唯一
> * `Prototype`：不放入实例化对象池、每次new出来的都不是同一个bean
> * `Request`：Request作用域内对象唯一 
> * `Session`：Session作用域内对象唯一

"BeanFactory is a central registry and confituration of application components" 

> bean的注册中心

"it is generally better to rely on Dependency Injection ("push" configuration) ... rather than use any form of "pull" configuration like a BeanFactory lookup. ... Spring's Dependency Injection functionality is implemented using this BeanFactory interface and its subinterfaces" 

> 建议使用DI（依赖注入）来获得需要的bean（push的方式），而不是通过查询BeanFactory等 （pull方式 ），DI是通过`BeanFactory`及其子接口来实现的

"... BeanFactory will load bean definitions stored in a configuration source (such as an XML document), and use the {@code org.springframework.beans} package to configure the beans."

> 通常bean配置在xml中、或者用注解来配置

"There are no constraints on how the definitions could be stored: LDAP, RDBMS, XML, properties file, etc"

> 但也可以把bean存储在`LDAP`、`RDBMS`, ... 中 ，并无限制

"In contrast to the methods in {@link ListableBeanFactory}, all of the. operations in this interface will also check parent factories if this is a {@link HierarchicalBeanFactory}. If a bean is not found in this factory instance, the immediate parent factory will be asked. Beans in this factory instance are supposed to override beans of the same name in any parent factory."

> `HierarchicalBeanFactory`：子容器可以访问父容器，父容器不能访问子容器。例如`Spring MVC 容器`是`Spring容器`的子容器，因此`Service`和`DAO`放在`Spring容器`中 、`Controller`要放在`Spring MVC`容器中。如果一个Factory同时实现`ListableBeanFactory`和`HierarchicalBeanFactory`，就会具有两个接口的特性.

## 4.  例子

### 例子1：Bean添加到容器并注入的过程

> 例子：[https://github.com/spring-cloud/spring-cloud-openfeign/blob/ffb17a45523f614a3a5bfa9564493cf0374edf34/spring-cloud-openfeign-core/src/main/java/org/springframework/cloud/openfeign/FeignClientFactoryBean.java](https://github.com/spring-cloud/spring-cloud-openfeign/blob/ffb17a45523f614a3a5bfa9564493cf0374edf34/spring-cloud-openfeign-core/src/main/java/org/springframework/cloud/openfeign/FeignClientFactoryBean.java)
> 
> ~~~java
> ...
> class FeignClientFactoryBean 
> 		implements FactoryBean<Object>, InitializingBean, ApplicationContextAware { 
> 	private Class<?> type; 	// 要生产什么类型的bean
>	...
>	@Override 
>	public Object getObject() throws Exception {
>		return getTarget();	// 返回生产的bean
>	}
>	...
>	<T> T getTarget() {
>		... // 生产bean
>		Targeter targeter = get(context, Target.class);
>		return (T) tqrgeter.target(this, builder, context, new HardCodedTarget<>(this.type, this.name, url));
>	}
>	...
>}
> ~~~
> 
> 这个类的应用场景如下：
> 
> ~~~java
> @FeignClient("填入某个服务注册地址")
> public interface IUserFeignService {
> 	@RequestMapping("/填入服务路径")
> 	User. getUserById(@param("id") Integer id);
> }
> 
> public class userController {
> 	@Autowired
> 	private IUserFeignService userService;  //把FeignClient  Bean注入到这里
> }
> ~~~
>
> 上面的注入是如何实现的？  
> 
> 1. 扫描路径下所有class获取到有@FeignClient声明的类
> 2. 使用反射获取“服务注册地址”，“路径”，“参数信息”
> 3. 通过FactoryBean提供实例化对象，类似下面的步骤
> 
> ~~~java
> Class clazz = IUserFeignService.class
> value =  clazz.getAnnotation(FeignClient.class).value();
> clazz.getMethods();
> //然后遍历得到的method，获取@RequestMapping的参数信息
> ~~~
> 
> 实现该注入过程的代码：[https://github.com/spring-cloud/spring-cloud-openfeign/blob/f9a8dd13f255b05ace30917048a6d12a6f7ad70a/spring-cloud-openfeign-core/src/main/java/org/springframework/cloud/openfeign/FeignClientsRegistrar.java](https://github.com/spring-cloud/spring-cloud-openfeign/blob/f9a8dd13f255b05ace30917048a6d12a6f7ad70a/spring-cloud-openfeign-core/src/main/java/org/springframework/cloud/openfeign/FeignClientsRegistrar.java)
> 
> ~~~java
> ...
> class FeignClientsRegistrar {
> 	...
> 	@Override
> 	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
> 		registerDefaultConfiguration(metadata, registry);
> 		registerFeignClients(metadata, registry);
> 	}
>	
> 	private void registerDefaultConfiguration(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
>		...
> 	}
>	
>	public void registerFeignClients(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
>		...
>		Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableFeignClients.class.getName());
>		AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(FeignClient.class);
>		...
>		if (clients == null || clients.length == 0). {
>			...
>			scanner.addIncludeFilter(new AnnotationTypeFilter(FeignClient.class));
>			...
>			for (String basePackage : basePackages) {
>				candidateComponents.addAll(scanner.findCandidateComponents(basePackage));
>			}
>		} else {
>			...
>		}	
>		
>		for (BeanDefinition candidateComponent : candidateComponents) {
>			if (candidateComponent instanceof AnnotatedBeanDefinition) {
>				AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
>				AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
>				...
>				Map<String, Object> attributes = annotationMetadata
>						.getAnnotationAttributes(FeignClient.class.getCanonicalName());
>				...
>				registerFeignClient(registry, annotationMetadata, attributes);
>			}
>		}
>	}
>
>	private void registerFeignClient(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata,
>			Map<String, Object> attributes) {
>		String className = annotationMetadata.getClassName();
> 		BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(FeignClientFactoryBean.class);
>		... //  添加 url,  path, 等各种attributes
>		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
>		...
>		BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[] { alias });
>		BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry); // 把FeignClientFactoryBean注册到容器中
>	}	
> }
> ~~~

### 例子2：父子容器

