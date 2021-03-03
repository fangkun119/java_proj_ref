<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Spring IOC：装配基础](#spring-ioc%E8%A3%85%E9%85%8D%E5%9F%BA%E7%A1%80)
  - [1 装配方式](#1-%E8%A3%85%E9%85%8D%E6%96%B9%E5%BC%8F)
  - [2 自动装配](#2-%E8%87%AA%E5%8A%A8%E8%A3%85%E9%85%8D)
  - [3 Java Config类](#3-java-config%E7%B1%BB)
  - [4 XML装配](#4-xml%E8%A3%85%E9%85%8D)
    - [4.1/4.2 构造函数注入，装配集合](#4142-%E6%9E%84%E9%80%A0%E5%87%BD%E6%95%B0%E6%B3%A8%E5%85%A5%E8%A3%85%E9%85%8D%E9%9B%86%E5%90%88)
    - [4.2 属性注入](#42-%E5%B1%9E%E6%80%A7%E6%B3%A8%E5%85%A5)
      - [(1) 介绍](#1-%E4%BB%8B%E7%BB%8D)
      - [(2) 方法](#2-%E6%96%B9%E6%B3%95)
      - [(3) 工具](#3-%E5%B7%A5%E5%85%B7)
  - [5 混合装配](#5-%E6%B7%B7%E5%90%88%E8%A3%85%E9%85%8D)
    - [5.1 自动装配](#51-%E8%87%AA%E5%8A%A8%E8%A3%85%E9%85%8D)
    - [5.2 Java Config类](#52-java-config%E7%B1%BB)
      - [(1) Configure类可以通过`@Import`注解来引入另一个Configure类中的bean](#1-configure%E7%B1%BB%E5%8F%AF%E4%BB%A5%E9%80%9A%E8%BF%87import%E6%B3%A8%E8%A7%A3%E6%9D%A5%E5%BC%95%E5%85%A5%E5%8F%A6%E4%B8%80%E4%B8%AAconfigure%E7%B1%BB%E4%B8%AD%E7%9A%84bean)
      - [(2) Configure类可以通过`@ImportResource`注解，将定义在XML中的Bean也引入进来](#2-configure%E7%B1%BB%E5%8F%AF%E4%BB%A5%E9%80%9A%E8%BF%87importresource%E6%B3%A8%E8%A7%A3%E5%B0%86%E5%AE%9A%E4%B9%89%E5%9C%A8xml%E4%B8%AD%E7%9A%84bean%E4%B9%9F%E5%BC%95%E5%85%A5%E8%BF%9B%E6%9D%A5)
    - [5.3 XML配置](#53-xml%E9%85%8D%E7%BD%AE)
      - [(1) XML配置文件可以通过`<import>`导入写在其他XML配置文件中的类，也可以通过`<bean>`标签导入Config类中的配置](#1-xml%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6%E5%8F%AF%E4%BB%A5%E9%80%9A%E8%BF%87import%E5%AF%BC%E5%85%A5%E5%86%99%E5%9C%A8%E5%85%B6%E4%BB%96xml%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6%E4%B8%AD%E7%9A%84%E7%B1%BB%E4%B9%9F%E5%8F%AF%E4%BB%A5%E9%80%9A%E8%BF%87bean%E6%A0%87%E7%AD%BE%E5%AF%BC%E5%85%A5config%E7%B1%BB%E4%B8%AD%E7%9A%84%E9%85%8D%E7%BD%AE)
  - [6 根配置](#6-%E6%A0%B9%E9%85%8D%E7%BD%AE)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Spring IOC：装配基础

[TOC]

## 1 装配方式

3种装配方式，建议按以下优先顺序

| 装配方式     | 特点                                                         |
| ------------ | ------------------------------------------------------------ |
| 隐式自动装配 | 配置少                                                       |
| JavaConfig类 | 类型安全比XML强大，不能隐式配置时优先使用（例如某些源代码不是自己维护的，不方便使用自动配置） |
| XML配置      | 想要使用XML命名空间提供便利，并且JavaConfig类没有同样的实现  |

有些场合必须用第2种甚至第3中装配方法，可以相互搭配混着用，最后写一个根配置用import的方式把各种子配置聚集在一起

## 2 自动装配

<div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_1_autowire.jpg" width="1024" /></div>

## 3 Java Config类

<div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_2_javaconfig.jpg" width="1024" /></div>

## 4 XML装配

### 4.1/4.2 构造函数注入，装配集合

<div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_3_xml_1.jpg" width="1024" /></div>

### 4.2 属性注入

#### (1) 介绍

<div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_3_xml_2.jpg" width="1024" /></div>

#### (2) 方法

<div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_3_xml_3.jpg" width="1024" /></div>

<div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_3_xml_4.jpg" width="1024" /></div>

#### (3) 工具

<div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_3_xml_5.jpg" width="1024" /></div>

## 5 混合装配

> 三种装配方法（隐式自动装配、JavaConfig类、XML装配）并不互斥，有些场景无法仅仅使用最喜欢的装配方式，必须混着用

### 5.1 自动装配

属性、`setter`、构造函数：只要使用`@Autowired`注解，就会注入三种方式声明的所有bean

> Spring会扫描所有的bean，不仅仅是@Component注解的类，也能够包含用JavaConfig类或者XML装配的类

### 5.2 Java Config类

#### (1) Configure类可以通过`@Import`注解来引入另一个Configure类中的bean

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_mix_1.jpg" width="1024" /></div>

#### (2) Configure类可以通过`@ImportResource`注解，将定义在XML中的Bean也引入进来               

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_mix_2.jpg" width="1024" /></div>

### 5.3 XML配置

#### (1) XML配置文件可以通过`<import>`导入写在其他XML配置文件中的类，也可以通过`<bean>`标签导入Config类中的配置

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_mix_3.jpg" width="1024" /></div>

## 6 根配置

> * 不论使用Java Config类，还是使用XML进行装配，都应当创建一个根配置（Root Configuration）来将其他的配置文件也组合进来
> * 同时也在根配置中启动组件扫描（通过`<context:component-scan>`或`@ComponentScan`)

