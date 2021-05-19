<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Spring Framework底层原理](#spring-framework%E5%BA%95%E5%B1%82%E5%8E%9F%E7%90%86)
  - [01 Bean的创建过程](#01-bean%E7%9A%84%E5%88%9B%E5%BB%BA%E8%BF%87%E7%A8%8B)
    - [(1) 例子代码](#1-%E4%BE%8B%E5%AD%90%E4%BB%A3%E7%A0%81)
    - [(2) Bean生命周期各阶段](#2-bean%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F%E5%90%84%E9%98%B6%E6%AE%B5)
      - [(a) Bean创建过程](#a-bean%E5%88%9B%E5%BB%BA%E8%BF%87%E7%A8%8B)
      - [(b) 与源码对应关系](#b-%E4%B8%8E%E6%BA%90%E7%A0%81%E5%AF%B9%E5%BA%94%E5%85%B3%E7%B3%BB)
        - [无参构造方法反射](#%E6%97%A0%E5%8F%82%E6%9E%84%E9%80%A0%E6%96%B9%E6%B3%95%E5%8F%8D%E5%B0%84)
        - [属性注入，初始化](#%E5%B1%9E%E6%80%A7%E6%B3%A8%E5%85%A5%E5%88%9D%E5%A7%8B%E5%8C%96)
        - [初始化：Bean初始化前、Bean初始化、Bean初始化后](#%E5%88%9D%E5%A7%8B%E5%8C%96bean%E5%88%9D%E5%A7%8B%E5%8C%96%E5%89%8Dbean%E5%88%9D%E5%A7%8B%E5%8C%96bean%E5%88%9D%E5%A7%8B%E5%8C%96%E5%90%8E)
  - [02 AOP](#02-aop)
    - [(1) 例子代码](#1-%E4%BE%8B%E5%AD%90%E4%BB%A3%E7%A0%81-1)
    - [(2) AOP原理](#2-aop%E5%8E%9F%E7%90%86)
      - [(a) AOP处理时机](#a-aop%E5%A4%84%E7%90%86%E6%97%B6%E6%9C%BA)
      - [(b) 步骤](#b-%E6%AD%A5%E9%AA%A4)
        - [步骤1：Spring判断UserService是否需要AOP](#%E6%AD%A5%E9%AA%A41spring%E5%88%A4%E6%96%ADuserservice%E6%98%AF%E5%90%A6%E9%9C%80%E8%A6%81aop)
        - [步骤2：找到所有的切面Bean](#%E6%AD%A5%E9%AA%A42%E6%89%BE%E5%88%B0%E6%89%80%E6%9C%89%E7%9A%84%E5%88%87%E9%9D%A2bean)
        - [步骤3：遍历切面Bean的所有方法的注解填入的表达式，是否与UserService的某个方法匹配](#%E6%AD%A5%E9%AA%A43%E9%81%8D%E5%8E%86%E5%88%87%E9%9D%A2bean%E7%9A%84%E6%89%80%E6%9C%89%E6%96%B9%E6%B3%95%E7%9A%84%E6%B3%A8%E8%A7%A3%E5%A1%AB%E5%85%A5%E7%9A%84%E8%A1%A8%E8%BE%BE%E5%BC%8F%E6%98%AF%E5%90%A6%E4%B8%8Euserservice%E7%9A%84%E6%9F%90%E4%B8%AA%E6%96%B9%E6%B3%95%E5%8C%B9%E9%85%8D)
      - [(c) 步骤3详解](#c-%E6%AD%A5%E9%AA%A43%E8%AF%A6%E8%A7%A3)
      - [(d) CGLib动态代理生成方式](#d-cglib%E5%8A%A8%E6%80%81%E4%BB%A3%E7%90%86%E7%94%9F%E6%88%90%E6%96%B9%E5%BC%8F)
  - [03 Spring事务](#03-spring%E4%BA%8B%E5%8A%A1)
    - [(1) 事务传播例子：Propagation.NEVER及错误用法](#1-%E4%BA%8B%E5%8A%A1%E4%BC%A0%E6%92%AD%E4%BE%8B%E5%AD%90propagationnever%E5%8F%8A%E9%94%99%E8%AF%AF%E7%94%A8%E6%B3%95)
    - [(2) @Transactional原理](#2-transactional%E5%8E%9F%E7%90%86)
    - [(3) 问题修复](#3-%E9%97%AE%E9%A2%98%E4%BF%AE%E5%A4%8D)
    - [(4) 事务传播例子：`Propagation.REQUIRES_NEW`](#4-%E4%BA%8B%E5%8A%A1%E4%BC%A0%E6%92%AD%E4%BE%8B%E5%AD%90propagationrequires_new)
  - [04 BeanPostProcessor](#04-beanpostprocessor)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Spring Framework底层原理

## 01 Bean的创建过程

### (1) 例子代码

> Test Driver
>
> ~~~java
> public class Test {
>    	public static void main(String[] args) {
>    		// Application Context
> 		AnnotationConfigApplictionContext appConext = new AnnotationConfigApplictionContext();
> 
>    		// 从容器中获取UserService Bean
> 		UserService userService = appContext.getBean("userService", UserService.class);
>         
>    		// 
> 		OrderService userService.getOrderService();	// 注入到UserService Bean中的OrderService
>    		userService.getDefaultUser();	        	// Post Construct阶段得到的属性值
> 		userService.runDemo();       				// 执行UserService Bean的方法
>    	}
> }
> ~~~
>
> UserService
>
> ~~~java
> @Component
> public class UserService implements InitializingBean {
> 	/*******************************************************
> 	* 属性
> 	*******************************************************/
> 	// 在对象构造阶段注入
> 	private ProfileService profileService;
> 
> 	// 在属性注入阶段赋值
> 	@Autowired
> 	private ConfigMapper cfgMapper;
> 	@Autowired
> 	private OrderService orderService;
> 
> 	// 在Bean初始化阶段为它赋值
> 	private User defaultUser;
>     
> 	/*******************************************************
> 	* 对象构造
> 	*******************************************************/    
> 	// 默认调用无参构造方法创建对象，除非有其他构造方法被@Autowired注解
> 	public UserService() {}; 
> 
> 	// 使用@Autowired注解来改用下面的有参构造方法来创建对象
> 	// 变量profileService注入
> 	// * 优先byType: 根据类型ProfileService.class来查找Bean，如果只有一个则直接注入
> 	// * 随后byName: 如过找到多个，根据名称profService来匹配并注入，无法匹配时报错
> 	// 单例Bean不等同于单例模式，同一个类型可以定义多个单例Bean
> 	// * 使用不同的bean name能拿到不同的Bean以及不同的对象
> 	// * 使用相同的bean name拿到的才是相同的对象
> 	@Autowired
> 	public UserService(ProfileService profService) {
> 		this.profileService = profService;
> 	}
> 
>     /*******************************************************
>     * Bean初始化前
>     *******************************************************/
>     @PostConstruct
>     public void init() {
> 		this.defaultUser = cfgMapper.getDefaultUser();
>     }
> 
>     /*******************************************************
>     * Bean初始化
>     *******************************************************/    
>     @Override
>     public void afterPropertiesSet() throws Exception {
>         // 该方法来自InitializingBean接口，在属性注入完成后执行
>         // 也可以在这里设置defaultUser
>         // this.defaultUser = cfgMapper.getDefaultUser();
>     }
> 
>     /*******************************************************
>     * 其他方法
>     *******************************************************/ 
>     public User getDefaultUser {
>         return defaultuser;
>     }
> }
> ~~~

### (2) Bean生命周期各阶段

#### (a) Bean创建过程

以上面的UserService Bean为例，它的创建经过以下阶段

> UserService.class
>
> → 使用`推断构造方法`来反射创建UserService对象
>
> * 默认：只调用无参构造方法（没有无参构造方法时会报错）
> * 使用@Autowired注解1个有参构造方法：调用这个被注解的方法（用@Autowired注解多个有参构造方法也会报错，Spring需要知道该调用哪个）并进行构造参数注入（见上面代码中的注解）
>
> → 属性注入
>
> → 初始化
>
> * Bean初始化前：执行使用`@PostConstruct`注解的方法
>
> * Bean初始化：如果UserService实现了`InitializingBean接口`，调用`afterPropertiesSet()`方法
>
> * Bean初始后
>     * 如果使用`AOP`，生成AOP[动态代理对象](../001_spring_ioc_aop/note/05_java_proxy.md)，把`代理对象`用作Bean对象放入容器
>     * 如果没有`AOP`，将生成的UserService对象放入容器

#### (b) 与源码对应关系

> 类：DefaultLisableFactory → AbstractAutowireCapableBeanFactory → AbstractBeanFactory
>
> 方法：createBean → doCreateBean

##### 无参构造方法反射

> AbstractAutowireCapableBeanFactory
>
> ~~~java
> if (instanceWrapper == null) {
>     instanceWrapper = createBeanInstance(beanName, mbd, args);
> }
> // 此处通过条件断点，可以看到bean的类型是UserService，其中的orderService等属性都是null
> final Object bean = instanceWrapper.getWarppedInstance();
> ~~~

##### 属性注入，初始化

> AbstractAutowireCapableBeanFactory
>
> ~~~java
> try {
>     // 属性注入：执行完这行之后，orderService等就已经被注入
>     populateBean(beanName, mbd, instanceWrapper);
>     // Bean初始化
>     exposedObject = initializeBean(beanName, exposedObject, mbd);
> }
> ~~~

##### 初始化：Bean初始化前、Bean初始化、Bean初始化后

> AbstractAutowireCapableBeanFactory.initializeBean方法
>
> ~~~java
> // 执行Aware
> invokeAwareMethods(beanName, bean);
> // Bean初始化前：会调用使用@PostConstruct注解的方法
> Obejct wrappedBean = bean;
> if (mbd == null || !mbd.isSynthetic()) {
>    	wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
> }
> // Bean初始化：会调用实现InitializingBean接口的afterPropertiesSet方法
> try {
>    	invokeInitMethods(beanName, wrappedBean, mbd);
> } catch (Throwable ex) { ... }
> // Bean初始化后：AOP代理在这里生成
> if (mbd == null || !mbd.isSynthetic()) {
>    	wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
> }
> ~~~
>
> Bean初始化：AbstractAutowireCapableBeanFactory.invokeInitMethods方法
>
> ~~~java
> // 如果实现了InitializingBean接口，会调用它的afterPropertiesSet方法
> boolean isInitializingBean = (bean instanceof InitializingBean);
> if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
>    	...
> 	((InitializingBean) bean).afterPropertiesSet(); // 调用afterPropertiesSet方法
> }
> ...
> // 如果在xml配置中指定了init-method，也会调用
> if (mbd != null && bean.getClass() != NullBean.class) {
>    	String initMethodName = mbd.getInitMethodName();
>    	if (StringUtils.hasLength(initMethodName) && 
>        	!(isInitializingBean && "afterPropertiesSet".equals(initMeghodName))) &&
>         	!mbd.isExternallyManagedInitMethod(initMethodName)) {
>    		invokeCustomInitMethod(beanName, bean, mbd);
>    	}
> }
> ~~~

## 02 AOP

### (1) 例子代码

> 配置
>
> ~~~java
> @EnableAspectJAutoProxy 	// 开启AOP动态代理
> @Configuration				// 
> @ComponentScan(...)			// 从哪个包里扫描Bean
> class AppConfig {
> 	@Bean
> 	public JdbcTemplate jdbcTemplate() {
> 		return new JdbcTemplate(dataSource());
> 	}
>     
> 	@Bean
> 	public DataSource dataSource() {
> 		DriverManagerDataSource dataSource = new DriverManagerDataSource();
> 		dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/mydb?...");
> 		dataSource.setUsername("root");
> 		dataSource.setPasswd("root");
> 		return dataSource;
> 	}
> }
> ~~~
>
> UserService
>
> ~~~java
> @Component
> public calss UserService {
> 	@Autowired
> 	private JdbcTemplate jdbcTemplate;
> 
> 	public void runDemo() {
> 		jdbcTemplate.execute("insert t1 values(1,1,1,1,1)");
> 		throw enw NullPointerException();
> 	}
> }
> 
> ~~~
>
> Aspect
>
> ~~~java
> @Aspect
> @Component
> public class PrintlnAspect {
>     @Before("execution(public void com.xxx.UserService.test)")
>     public void runBefore(JoinPoint joinPoint) {
>         System.out.println("before running method");
>         // 被代理的原生对象
>         Object target = joinPoint.getTarget();
>     }
> }
> ~~~
>
> Test Driver
>
> ~~~java
> public class Test {
> 	public static void main(String[] args) {
> 		AnnotationConfigApplictionContext appConext = new AnnotationConfigApplictionContext();
> 		UserService userService = appContext.getBean("userService", UserService.class);
> 		userService.runDemo();       				// 执行UserService Bean的方法
> 	}
> }
> ~~~

### (2) AOP原理

#### (a) AOP处理时机

时机：`初始化后`

> 对象构造 → 属性注入 → 初始化前 → 初始化 → `初始化后` → 放入容器

对应代码：AbstractAutowireCapableBeanFactory

> ~~~java
> if (mbd == null || !mbd.isSynthetic()) {
> 	// 初始化AOP
> 	// * 传入的是原始的对象
> 	// * 返回的有可能是AOP代理
> 	wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
> }
> ~~~

#### (b) 步骤

##### 步骤1：Spring判断UserService是否需要AOP

> (1) Configuration类被`@EnableAspectJAutoProxy`注解
>
> (2) 有切面Bean指定要为UserService的某个方法添加切面

##### 步骤2：找到所有的切面Bean

##### 步骤3：遍历切面Bean的所有方法的注解填入的表达式，是否与UserService的某个方法匹配

#### (c) 步骤3详解

`applyBeanPostProcessorsAfterInitialization`

> 遍历所有的BeanPostProcessor
>
> * 调用每个processor的postProcessAfterInitialization方法
>
> * 将bean进行包装处理变成另一个Bean

`postProcessAfterInitialization` → `wrapIfNecessary`

> 检查
>
> * `isInfrastructureClass`：CutPoint、Advice、Advisor、AopInfrastructureBean等不需要进行AOP
>
> * `shouldSkip`：不需要AOP
>
> * `getAdvicesAndAdvisorsForBean`：获取切面通知、没有Advice的不需要AOP
>
> 生成代理对象（动态代理）
>
> * `createProxy`：使用Advice创建代理对象并返回，替代原始bean

#### (d) CGLib动态代理生成方式

> 以上面的UserService为例，CGLib自动生成如下代理类
>
> ~~~java
> class UserServiceProxy extends UserService {
>     // 不会被注入，值为null
> 	private JdbcTemplate jdbcTemplate;
> 	
> 	// 被代理对象
>     UserService userService;
>     
>     public void runDemo() {
>         // 执行切面的逻辑
>         // 执行原始Bean的业务逻辑
>         userService.runDemo();
>         // 执行切面的逻辑
>     }
> }
> ~~~

## 03 Spring事务

### (1) 事务传播例子：Propagation.NEVER及错误用法

> 配置
>
> ~~~java
> @EnableTransactionManagement 	// 开启事务
> @Configuration					// 必须添加@Configuration类
> @ComponentScan(...)				// 从哪个包里扫描Bean
> class AppConfig {
> 	// @Configuration注解使得Spring可以拦截dataSource()调用，为Proxy和JdbcTemplate注入同一个Singleton Bean
> 	// 原理：添加@Configuration注解之后，当调用方法（例如dataSource())时，会先查找是否有名为dataSource的Bean
> 	// * 有：会传入这个bean
> 	// * 无：直接调用方法
> 
> 	// 事务管理Bean
> 	@Bean
> 	public PlatformTransactionManager transactionManager() {
> 		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
> 		// 这里注入：代理类里面用来创建事务建立数据库连接所用到的dataSource
> 		transactionManager.setDataSource(dataSource()); 
> 		return transactionManager;
> 	}
> 
> 	@Bean
> 	public JdbcTemplate jdbcTemplate() {
> 		// 这里注入：用来执行业务SQL的dataSource
> 		return new JdbcTemplate(dataSource());
> 	}
> 
> 	@Bean
> 	public DataSource dataSource() {
> 		DriverManagerDataSource dataSource = new DriverManagerDataSource();
> 		dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/mydb?...");
> 		dataSource.setUsername("root");
> 		dataSource.setPasswd("root");
> 		return dataSource;
> 	}
> }
> ~~~
>
> UserService
>
> ~~~java
> @Component
> public calss UserService {
> 	@Autowired
> 	private JdbcTemplate jdbcTemplate;
> 
> 	@Transactional
> 	public void testTrx() {
> 		jdbcTemplate.execute("insert t1 values(1,1,1,1,1)");
> 		// 原本期望会抛异常，而实际运行时，Propagation注解失效没有抛异常
> 		// * 因为调用的是原生对象的方法，而不是AOP代理的方法
> 		// * 具体可参考上一小节AOP的原理，或下面的说明
> 		expectExceptionButNotThrow();
> 	}
>     
> 	// 注解的用途是：不容许事务传播，即如果它在一个事务中运行，那么会抛异常
> 	@Transactional(propagation = Propagation.NEVER)
> 	public void expectExceptionButNotThrow() {
> 		...
> 	}
> }
> ~~~
>
> Test Driver
>
> ~~~java
> public class Test {
> 	public static void main(String[] args) {
> 		AnnotationConfigApplictionContext appConext = new AnnotationConfigApplictionContext();
> 		UserService userService = appContext.getBean("userService", UserService.class);
> 		userService.testTrx();  // 执行UserService Bean的方法
> 	}
> }
> ~~~

### (2) @Transactional原理

> 为配置类@Configuration和@EnableTransactionManagement、配置TransactionManager Bean之后，Spring会开启事务功能、为包含@Transactional方法的类生成代理，形式如下
>
> ~~~java
> class UserServiceProxy extends UserService {
>     UserService userService;
> 
>     public void testTrx() {
>         // 1. 建立数据库连接
>         ...
>         // 2. 关闭auto commit
> 		conn.setAutoCommit(false);
>         // 3. 执行被代理的方法
>         userService.testTrx();
>         // 4. 提交事务（如果上一步没有抛异常）
>         conn.commit();
>     }
> 
>     public void expectExceptionButNotThrow() {
>         ...
>         userService.expectExceptionButNotThrow();
>         ...
>     }
> }
> ~~~
>
> 其中@Configuration和@EnableTransactionManagement注解都需要添加，原因见上面的代码注释

### (3) 问题修复

> 如何让`(1)小节`中的`@Transactional(propagation = Propagation.NEVER)`注解起作用
>
> * 需要让代码调用代理对象的方法，而不是原生对象的方法
> * 通过自己注入自己可以做到这一点
>
> UserService的代码修改为
>
> ~~~ java
> @Component
> public calss UserService {
> 	@Autowired
> 	private JdbcTemplate jdbcTemplate;
>     
> 	// 自己注入自己，这样得到的userService是一个代理对象，而不是原生对象
> 	@Autowired
>     private UserService userService;
> 
> 	@Transactional
> 	public void testTrx() {
> 		jdbcTemplate.execute("insert t1 values(1,1,1,1,1)");
> 
> 		// 直接调用会调用原生对象的方法，导致willThrowException方法的注解失效
> 		// willThrowException();
> 		
> 		// 需要通过注入的代理对象（就是它自己）来调用
> 		userService.willThrowException();
> 	}
>     
> 	// 注解的用途是：不容许事务传播，即如果它在一个事务中运行，那么会抛异常
> 	@Transactional(propagation = Propagation.NEVER)
> 	public void willThrowException() {
> 		...
> 	}
> }
> ~~~

### (4) 事务传播例子：`Propagation.REQUIRES_NEW`

> `Propagation.REQUIRES_NEW`：被注解的方法，如果在另一个事务中运行，会被放在一个嵌套其中的新的事务里执行
>
> ~~~java
> @Component
> public calss UserService {
> 	@Autowired
> 	private JdbcTemplate jdbcTemplate;
>     
> 	// 自己注入自己，这样得到的userService是一个代理对象，而不是原生对象
> 	@Autowired
>     private UserService userService;
> 
> 	@Transactional
> 	public void testTrx() {
> 		jdbcTemplate.execute("insert t1 values(1,1,1,1,1)");
> 		userService.willThrowException();
> 	}
>     
> 	// 注解的用途是：不容许事务传播，即如果它在一个事务中运行，那么会抛异常
> 	@Transactional(propagation = Propagation.REQUIRES_NEW)
> 	public void willThrowException() {
> 		...
> 		throw new 
> 	}
> }
> ~~~
>
> 放在Spring事务实现的角度上来理解，调用上面代码中的`testTrx()`方法，两个方法都会被认为抛异常，虽然是两个事务，但是都不会提交

## 04 BeanPostProcessor

BeanPostProcessor是一个接口，有很多实现类，被用在Bean创建过程中的各个环节

> 以之前的UserService Bean为例，创建过程如下
>
> → UserService.class
>
> → 使用`推断构造方法`来反射创建对象
>
> → 属性注入：使用`AutowiredAnnotationBeanPostProcessor`
>
> → 初始化 
>
> * Bean初始化前：使用BeanPostProcessor
>
> * Bean初始化：使用BeanPostProcessor
>
> * Bean初始化后：使用`AnnotationAwareAsectJAutoProxyCreator`

