# Spring Bean生命周期及各阶段的自定义操作

## 1 对象和Bean

> ~~~java
> AnnotationConfigApplicationContext context = new 
> AnnotationConfigApplicationContext(AppConfig.class);
> 
> UserService svc1 = context.getBean("userService",
> UserService.class);
> UserService svc2 = new UserService();
> ~~~

> * 使用`svc1`可以给UserService加`@autowired`成员变量并自动装配；而`svc2`则不可以为该成员自动装配、只能拿到null
> * Spring在实例化bean的时候，会为它填充属性，而这2步也是bean生命周期中的2步

## 2 Bean的生命周期（Singleton Bean为例）

> 1. 载入`.class`生成Class类
> 2. 生成Bean Defination（bean class）
> 3. 创建类对象
> 4. 填充属性
> 5. `Aware`：回调对象中提供的方法、来为Spring设置Bean Defination信息
>    * 例如调用BeanNameAware接口的setBeanName方法来设置bean name
> 6. Bean初始化
> 7. AOP
> 8. 将bean对象放入单例池：
>    * 未开启AOP时：Map<BeanName，对象>
>    * 开启了AOP时：Map<BeanName，AOP代理对象>
> 9. 使用bean时通过getBean方法获取这个bean的实例

## 3 各阶段介绍及相关自定义操作

### 3.1 Bean Defination

Spring提供`BeanDefination`接口（`org.springframework.beans.factory.config.BeanDefination`）用来描述bean的定义

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/080_spring_concept/spring_bean_defination.jpg)

> 例如：

> * `constructorArgumentValues`：构造方法的参数值
> * `scope`：bean的target scope
> * `lazyInit`: 是否需要懒加载
> * `autowireMode`：注入模型，by type，by name等
> * `beanClass`：bean的类型

在这个阶段、Spring框架会生成Bean Defination对象，并扫描bean的定义信息（xml或注解）来填充信息

### 3.2 Aware阶段

> 执行预设的回调函数、来让业务代码知道（aware）与这个Bean相关的信息。可以让Bean实现以下接口来设置这些回调函数

(1) 让一个Bean得到自己的`bean name` ：实现`BeanNameAware`接口，提供方法给Spring框架回调

> ~~~java
> @Component
> public class MyBean implements BeanNameAware {
>   private String beanName;
>   @override
>   public void setBeanName(String beanName) {
>     this.beanName = beanName;
>   }
> }
> ~~~

(2) 让一个Bean知道自己是被哪个bean factory创建的：实现BeanFactoryAware接口，提供方法给Spring框架回调

> ~~~java
> @Component
> public class MyBean implements BeanFactoryAware {
>   private BeanFactory beanFactory;
>   @override
>   public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
>     this.beanFactory = beanFactory;
>   }
> }
> ~~~

(3) 类似的接口还有`ApplicationContextAware`

### 3.3 Bean初始化

可以让Bean实现InitializingBean，在初始化阶段加入自定义的代码

> ~~~java
> @Component 
> public class MyBean implements InitializingBean {
>   @override
>   public void afterPropertiesSet() throws Exception {
>     // 这个方法在Spring为这个bean填充完属性之后执行
>     // 由Spring来负责Bean字段的自动装配
>     // 而随后的业务初始化代码可以放在这个方法内
>   }
> }
> ~~~

也可以通过在`xml`配置中指定`init-method`来为Bean指定初始化方法

### 3.4 AOP

如果开启了AOP（例如下面的代码），会在这个阶段生成AOP代理对象

> ~~~java
> package com.javaprojref.sprintconcept.beanlifecycledemo;
> ...
> 
> @Component
> public class MyService {
>   @Autowired
>   private MyBean myBean;
>   public void test {
>     
>   }
> }
> ~~~
>
> 顶部初始化`AnnotationConfigApplicationContext`时使用的`AppConfig`类
>
> ~~~java
> @Configuration
> @ComponentScan(com.javaprojref.sprintconcept.beanlifecycledemo) 
> @EnableAspectJAutoProxy //开启AOP
> public class AppConfig {
>   
> }
> ~~~
>
> AOP切面
>
> ~~~java
> @Aspect
> @Component
> public class MyAspect {
>   
>   @Around("execution(com.javaprojref.sprintconcept.beanlifecycledemo.MyService.test())")
>     public String invoke(ProceedingJoinPoint point) {
>       try {
>         System.out.println("MyAspect.invoke");
>         return (String) point.proceed();
>       } catch (Throwable e) {
>         e.printStackTrace();
>       }
>       return null;
>     }
> }
> ~~~
