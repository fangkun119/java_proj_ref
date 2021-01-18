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

![](https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_1_autowire.jpg)

## 3 Java Config类

![](https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_2_javaconfig.jpg)

## 4 XML装配

### 4.1/4.2 构造函数注入，装配集合

![](https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_3_xml_1.jpg)

### 4.2 属性注入

#### (1) 介绍

![](https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_3_xml_2.jpg)

#### (2) 方法

![](https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_3_xml_3.jpg)

![](https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_3_xml_4.jpg)

#### (3) 工具

![](https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_3_xml_5.jpg)

## 5 混合装配

> 三种装配方法（隐式自动装配、JavaConfig类、XML装配）并不互斥，有些场景无法仅仅使用最喜欢的装配方式，必须混着用

### 5.1 自动装配

属性、`setter`、构造函数：只要使用`@Autowired`注解，就会注入三种方式声明的所有bean

> Spring会扫描所有的bean，不仅仅是@Component注解的类，也能够包含用JavaConfig类或者XML装配的类

### 5.2 Java Config类

#### (1) Configure类可以通过`@Import`注解来引入另一个Configure类中的bean

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_mix_1.jpg)

#### (2) Configure类可以通过`@ImportResource`注解，将定义在XML中的类也import进来               

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_mix_2.jpg)

### 5.3 XML配置

#### (1) XML配置文件可以通过`<import>`导入写在其他XML配置文件中的类，也可以通过`<bean>`标签导入Config类中的配置

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_ioc/spring_ioc_approach_mix_3.jpg)

## 6 根配置

* 不论使用Java Config类，还是使用XML进行装配，都应当创建一个根配置（Root Configuration）来将其他的配置文件也组合进来
* 同时也在根配置中启动组件扫描（通过`<context:component-scan>`或`@ComponentScan`)

