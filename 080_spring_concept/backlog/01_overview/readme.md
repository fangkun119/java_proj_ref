# 01 概览


## 1. 依赖注入原理

### (1) Demo 01 A

[`demo1reflection/UserController.java`](https://github.com/fangkun119/java_proj_ref/blob/master/080_spring_concept/01_overview/demo01_reflect_autowire/src/main/java/com/javaprojref/springinternal/demo1reflection/UserController.java)：模拟Controller类

~~~java
package com.javaprojref.springinternal.demo1reflection;

public class UserController {
    private UserService userService;
    public UserService getUserService() {
        return userService;
    }
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
~~~

[`demo1reflection/DemoTest.java`](https://github.com/fangkun119/java_proj_ref/blob/master/080_spring_concept/01_overview/demo01_reflect_autowire/src/test/java/test/javaprojref/springinternal/demo1reflection/DemoTest.java) ：四个demo

> 1. 使用`class.getDeclareFields()`获得私有成员变量
> 2. 默认情况下无法通过反射来直接访问私有成员变量
> 3. 借助`field.setAccessible(true)`, 可以解除该限制，直接set私有变量，这也是@autowired实现依赖注入时所用到的方法
> 4. 约定field name和get,set方法的命名方式，通过field name找到set方法并调用，这也是属性注入所用到的方法


### (2) Demo 02 B

[`demo2autowire/Autowired.java`](https://github.com/fangkun119/java_proj_ref/blob/master/080_spring_concept/01_overview/demo01_reflect_autowire/src/main/java/com/javaprojref/springinternal/demo2autowire/Autowired.java)：自定义的`@Autowired`注解

> ~~~java
> @Retention(RetentionPolicy.RUNTIME)
> @Target(ElementType.FIELD)
> public @interface Autowired {
> }
> ~~~

[`demo2autowire/UserController.java`](https://github.com/fangkun119/java_proj_ref/blob/master/080_spring_concept/01_overview/demo01_reflect_autowire/src/main/java/com/javaprojref/springinternal/demo2autowire/UserController.java)
> 
> ~~~java
> package com.javaprojref.springinternal.demo2autowire;
> 
> public class UserController {
>     // 用@Autowired注解的私有成员变量，没有set方法
>     @Autowired
>     private UserService userService;
>     public UserService getUserService() {
>         return userService;
>     }
> 
>     // 没有使用Autowired注解的私有成员变量
>     private Integer notAutowired;
> }
> ~~~ 

[`demo2autowire/Demo2AutowiredTest.java`](https://github.com/fangkun119/java_proj_ref/blob/master/080_spring_concept/01_overview/demo01_reflect_autowire/src/test/java/test/javaprojref/springinternal/demo2autowire/Demo2AutowiredTest.java)

> 对使用`@Autowired`注解的成员变量进行依赖注入（缺少bean容器，用默认构造函数模拟替代bean容器)

## 2. 可扩展的`bean`容器

> `Bean容器`：存储、管理和获取bean，例如按照`id`，`类型`取到`bean`以便进行注入

bean容器从以下几个角度来增强可扩展性：

> 1. `bean defination`）可扩展：通过注解和XML配置
> 2. 解析`bean defination`之后，通过一组`processor`，做到生成`bean data`的环节可扩展
> 3. 得到`bean data`，开始创建`bean对象`时，在创建前后植入两组`processor`，做到`bean对象`创建可扩展
> 4. 在不同阶段触发不同事件，通过为这些事件注册`Observer`来扩展
> 5. 面向接口编程

## 3. Spring 源码

~~~xml
<!-- https://mvnrepository.com/artifact/org.springframework/spring-context -->
<dependency>
	<groupId>org.springframework</groupId>
      <artifactId>spring-context</artifactId>
      <version>4.3.24.RELEASE</version>
</dependency>
~~~

加载到的`Spring`依赖

![](https://raw.githubusercontent.com/kenfang119/pics/main/080_spring_concept/spring_context_and_deps.jpg)

> * `META-INF/spring-handlers`, `META-INF/spring-schemas`：[https://stackoverflow.com/questions/8609322/need-understanding-of-spring-handlers-and-spring-schemas](https://stackoverflow.com/questions/8609322/need-understanding-of-spring-handlers-and-spring-schemas) 

> * `validation`：数据校验，参考[https://www.cnblogs.com/liaojie970/p/9036349.html](https://www.cnblogs.com/liaojie970/p/9036349.html) 
> * `validation`：	实现用于参数检查的各种注解，例如@NotNull等
> * `ejb` 不用，`jndi`很少用，在微服务中不用

## 4. 参考资料

> * [使用 spring validation 完成数据后端校验](https://www.cnkirito.moe/spring-validation/)



