[TOC]

# Spring Framework底层原理

## 01 Bean生命周期

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
>     wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
> }
> // Bean初始化：会调用实现InitializingBean接口的afterPropertiesSet方法
> try {
>     invokeInitMethods(beanName, wrappedBean, mbd);
> } catch (Throwable ex) { ... }
> // Bean初始化后：AOP代理在这里生成
> if (mbd == null || !mbd.isSynthetic()) {
>     wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
> }
> ~~~
>
> Bean初始化：AbstractAutowireCapableBeanFactory.invokeInitMethods方法
>
> ~~~java
> // 如果实现了InitializingBean接口，会调用它的afterPropertiesSet方法
> boolean isInitializingBean = (bean instanceof InitializingBean);
> if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
>     ...
> 	((InitializingBean) bean).afterPropertiesSet(); // 调用afterPropertiesSet方法
> }
> ...
> // 如果在xml配置中指定了init-method，也会调用
> if (mbd != null && bean.getClass() != NullBean.class) {
>     String initMethodName = mbd.getInitMethodName();
>     if (StringUtils.hasLength(initMethodName) && 
>        		!(isInitializingBean && "afterPropertiesSet".equals(initMeghodName))) &&
>         	!mbd.isExternallyManagedInitMethod(initMethodName)) {
>         invokeCustomInitMethod(beanName, bean, mbd);
>     }
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

### (1) 例子代码

> 配置
>
> ~~~java
> @EnableTransactionManagement 	// 开启事务
> @Configuration					// 
> @ComponentScan(...)				// 从哪个包里扫描Bean
> class AppConfig {
> 	// 事务管理Bean
> 	@Bean
> 	public PlatformTransactionManager transactionManager() {
> 		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
> 		transactionManager.setDataSource(dataSource());
> 		return transactionManager;
> 	}
> 
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
> 	@Transactional
> 	public void runDemo() {
> 		jdbcTemplate.execute("insert t1 values(1,1,1,1,1)");
> 		throw enw NullPointerException();
> 	}
> }
> 
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

### 
