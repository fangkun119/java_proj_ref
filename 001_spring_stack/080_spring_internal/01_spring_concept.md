[TOC]

# Spring Framework底层原理

## 01 Bean生命周期

### (01) 例子代码

> Test Driver
>
> ~~~java
> public class Test {
>     public static void main(String[] args) {
>         // Application Context
> 		AnnotationConfigApplictionContext appConext = new AnnotationConfigApplictionContext();
> 
>         // 从容器中获取UserService Bean
> 		UserService userService = appContext.getBean("userService", UserService.class);
>         
>         // 
> 		OrderService userService.getOrderService();	// 注入到UserService Bean中的OrderService
>         userService.getDefaultUser();	        	// Post Construct阶段得到的属性值
> 		userService.runDemo();       				// 执行UserService Bean的方法
>     }
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

### (02) Bean生命周期各阶段

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

