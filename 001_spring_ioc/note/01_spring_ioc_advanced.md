# Spring IOC：高级装配技巧

[TOC]

## 1 Profile

### 1.1 用途

> 在运行时指定生效的bean配置，例如dev、qa、online环境分开

### 1.2 步骤

#### 步骤1：指定bean属于哪一个profile

##### (1) Java Config类

> Spring 3.1只能在Java Config类上使用@Profile注解，Spring 3.2开始可以在Java Config类的方法上使用@Profile注解
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_profile_1.jpg)

##### (2) XML配置

> 可通最外层<beans>的profile属性标记整个XML属于哪个Profile
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_profile_2.jpg)
>
> 可通过单个<bean>的profile属性标记某个bean属于哪个Profile
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_profile_3.jpg)

### 步骤2：激活profile

通过`spring.profiles.default`或`spring.profiles.active`来选择激活哪个profile

> * 首选使用`spring.profiles.active`属性，次选`spring.profiles.default`属性
> * 在集成测试类上，可以使用`@ActiveProfiles`注解来设置

## 2 条件化Bean

## 3 处理自动装配歧义

## 4 Bean作用域

> 略，见《...》

## 5 运行时注入



