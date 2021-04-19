<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Spring IOC：高级装配技巧](#spring-ioc%E9%AB%98%E7%BA%A7%E8%A3%85%E9%85%8D%E6%8A%80%E5%B7%A7)
  - [1 Profile](#1-profile)
    - [1.1 指定Bean属于哪一个profile](#11-%E6%8C%87%E5%AE%9Abean%E5%B1%9E%E4%BA%8E%E5%93%AA%E4%B8%80%E4%B8%AAprofile)
      - [(1) 在`@Configuration`类和其中的方法上使用`@Profile`注解](#1-%E5%9C%A8configuration%E7%B1%BB%E5%92%8C%E5%85%B6%E4%B8%AD%E7%9A%84%E6%96%B9%E6%B3%95%E4%B8%8A%E4%BD%BF%E7%94%A8profile%E6%B3%A8%E8%A7%A3)
      - [(2) `XML`配置](#2-xml%E9%85%8D%E7%BD%AE)
    - [1.2 激活profile](#12-%E6%BF%80%E6%B4%BBprofile)
  - [2 条件化Bean](#2-%E6%9D%A1%E4%BB%B6%E5%8C%96bean)
    - [2.1 方法](#21-%E6%96%B9%E6%B3%95)
    - [2.2 提供给Condition类的数据](#22-%E6%8F%90%E4%BE%9B%E7%BB%99condition%E7%B1%BB%E7%9A%84%E6%95%B0%E6%8D%AE)
  - [3 处理自动装配歧义](#3-%E5%A4%84%E7%90%86%E8%87%AA%E5%8A%A8%E8%A3%85%E9%85%8D%E6%AD%A7%E4%B9%89)
    - [3.1  Primary Bean：声明某个Bean是首选](#31--primary-bean%E5%A3%B0%E6%98%8E%E6%9F%90%E4%B8%AAbean%E6%98%AF%E9%A6%96%E9%80%89)
    - [3.2 `@Qualifier`限定符：声明要装配指定名称的Bean](#32-qualifier%E9%99%90%E5%AE%9A%E7%AC%A6%E5%A3%B0%E6%98%8E%E8%A6%81%E8%A3%85%E9%85%8D%E6%8C%87%E5%AE%9A%E5%90%8D%E7%A7%B0%E7%9A%84bean)
    - [3.3 使用Bean特征描述符，指定要什么样的Bean](#33-%E4%BD%BF%E7%94%A8bean%E7%89%B9%E5%BE%81%E6%8F%8F%E8%BF%B0%E7%AC%A6%E6%8C%87%E5%AE%9A%E8%A6%81%E4%BB%80%E4%B9%88%E6%A0%B7%E7%9A%84bean)
  - [4 Bean Scope（生命周期、作用域）](#4-bean-scope%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F%E4%BD%9C%E7%94%A8%E5%9F%9F)
    - [4.1 四种Bean Scope及设置方法](#41-%E5%9B%9B%E7%A7%8Dbean-scope%E5%8F%8A%E8%AE%BE%E7%BD%AE%E6%96%B9%E6%B3%95)
    - [4.2 Session Bean注入到Singleton Bean时，如何保持Session特性](#42-session-bean%E6%B3%A8%E5%85%A5%E5%88%B0singleton-bean%E6%97%B6%E5%A6%82%E4%BD%95%E4%BF%9D%E6%8C%81session%E7%89%B9%E6%80%A7)
  - [5 注入运行期的值](#5-%E6%B3%A8%E5%85%A5%E8%BF%90%E8%A1%8C%E6%9C%9F%E7%9A%84%E5%80%BC)
    - [5.1 方法](#51-%E6%96%B9%E6%B3%95)
    - [5.2 使用`属性占位符`注入](#52-%E4%BD%BF%E7%94%A8%E5%B1%9E%E6%80%A7%E5%8D%A0%E4%BD%8D%E7%AC%A6%E6%B3%A8%E5%85%A5)
      - [(1) 在@Configure类中使用](#1-%E5%9C%A8configure%E7%B1%BB%E4%B8%AD%E4%BD%BF%E7%94%A8)
      - [(2) 在XML配置中使用](#2-%E5%9C%A8xml%E9%85%8D%E7%BD%AE%E4%B8%AD%E4%BD%BF%E7%94%A8)
      - [(3) 在自动装配中使用](#3-%E5%9C%A8%E8%87%AA%E5%8A%A8%E8%A3%85%E9%85%8D%E4%B8%AD%E4%BD%BF%E7%94%A8)
    - [5.3 使用`SPEL`（Spring表达式）注入](#53-%E4%BD%BF%E7%94%A8spelspring%E8%A1%A8%E8%BE%BE%E5%BC%8F%E6%B3%A8%E5%85%A5)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Spring IOC：高级装配技巧

## 1 Profile

> 在运行时指定生效的bean配置，例如dev、qa、online环境分开

### 1.1 指定Bean属于哪一个profile

#### (1) 在`@Configuration`类和其中的方法上使用`@Profile`注解

Spring 3.2开始可以在`方法`上使用@Profile注解（3.1及之前版本只能在`类`上面使用@Profile注解）

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_profile_1.jpg" width="1024" /></div>

#### (2) `XML`配置

可通过最外层`<beans>`的profile属性标记整个XML属于哪个Profile

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_profile_2.jpg" width="1024" /></div>

可通过单个`<bean>`的profile属性标记某个bean属于哪个Profile

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_profile_3.jpg" width="1024" /></div>

### 1.2 激活profile

通过`spring.profiles.default`或`spring.profiles.active`配置项来选择激活哪个profile

> * 首选使用`spring.profiles.active`属性，
> * 次选`spring.profiles.default`属性
> * 在集成测试类上，可以使用`@ActiveProfiles`注解来设置

它们通过`Configuration Properties`来实现，因此可以配置在`JVM参数`、`环境变量`、`微服务的config server`、`@ActiveProfile`、`application.properties`、`application.yml`等各种位置来配置

## 2 条件化Bean

> 让Bean只有在满足某个条件时才会被创建

### 2.1 方法

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_condition_bean_approach.jpg" width="1024" /></div>

### 2.2 提供给Condition类的数据

> 提供给`matches`方法的两个参数所携带的数据如下
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_condition_bean_parameters.jpg" width="1024" /></div>

## 3 处理自动装配歧义

> 当需要自动装配一个bean，而容器中存在多个候选时，需要指定该装配哪个Bean，方法如下：

### 3.1  Primary Bean：声明某个Bean是首选

方法如下

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_primary_bean.jpg" width="1024" /></div>

### 3.2 `@Qualifier`限定符：声明要装配指定名称的Bean

例如下面的代码指定要装配名称为"icecream"的Bean

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_qualifier.jpg" width="1024" /></div>

### 3.3 使用Bean特征描述符，指定要什么样的Bean

例子如下

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_bean_qualifier_2.jpg" width="1024" /></div>

## 4 Bean Scope（生命周期、作用域）

### 4.1 四种Bean Scope及设置方法

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/001_spring_bean_scope.jpg" width="1024" /></div>

### 4.2 Session Bean注入到Singleton Bean时，如何保持Session特性

方法

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_inject_session_bean_into_singleton_bean.jpg" width="1024" /></div>

代码编写

> 使用@Scope属性标记bean的作用域，除了要标记作用域是Session，还要标记如何生成代理
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_inject_session_bean_into_singleton.jpg" width="1024" /></div>

关于Session

> [https://www.infoq.cn/article/Next-Generation-Session-Management-with-Spring-Session/](https://www.infoq.cn/article/Next-Generation-Session-Management-with-Spring-Session/)

## 5 注入运行期的值

> 想把运行期的值注入到bean中，但是`@Bean`/`@Component`/`XML配置的c:命名空间属性`都只能注入编译期常量

### 5.1 方法

> 方法1：用`属性占位符`来传入（使用Configure Properties）配置的常量值（例如配置文件或环境变量中的配置项）
>
> 方法2：用`Spring表达式（SPEL）`将某些计算结果注入

### 5.2 使用`属性占位符`注入

#### (1) 在@Configure类中使用

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_runtimeinject_properties.jpg" width="1024" /></div>

#### (2) 在XML配置中使用

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_inject_props_in_xml.jpg" width="1024" /></div>

#### (3) 在自动装配中使用

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_inject_props_in_auto.jpg" width="1024" /></div>

### 5.3 使用`SPEL`（Spring表达式）注入

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_runtime_injection_by_spel.jpg" width="1024" /></div>