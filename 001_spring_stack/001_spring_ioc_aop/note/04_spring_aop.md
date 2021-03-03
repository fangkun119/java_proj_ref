<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Spring AOP](#spring-aop)
  - [1. 基础原理](#1-%E5%9F%BA%E7%A1%80%E5%8E%9F%E7%90%86)
    - [1.1 AOP原理](#11-aop%E5%8E%9F%E7%90%86)
    - [1.2 Spring AOP](#12-spring-aop)
      - [(1) 和传统基于代理的AOP对比](#1-%E5%92%8C%E4%BC%A0%E7%BB%9F%E5%9F%BA%E4%BA%8E%E4%BB%A3%E7%90%86%E7%9A%84aop%E5%AF%B9%E6%AF%94)
      - [(2) 对比Spring AOP和AspectJ](#2-%E5%AF%B9%E6%AF%94spring-aop%E5%92%8Caspectj)
  - [2 使用`@AspectJ`注解定义Spring AOP切面](#2-%E4%BD%BF%E7%94%A8aspectj%E6%B3%A8%E8%A7%A3%E5%AE%9A%E4%B9%89spring-aop%E5%88%87%E9%9D%A2)
    - [2.1 编写切入点](#21-%E7%BC%96%E5%86%99%E5%88%87%E5%85%A5%E7%82%B9)
      - [(1) 方法](#1-%E6%96%B9%E6%B3%95)
      - [(2) 指示器](#2-%E6%8C%87%E7%A4%BA%E5%99%A8)
    - [2.2 编写切面](#22-%E7%BC%96%E5%86%99%E5%88%87%E9%9D%A2)
      - [(1) 方法](#1-%E6%96%B9%E6%B3%95-1)
      - [(2) 用于选择切点的注解](#2-%E7%94%A8%E4%BA%8E%E9%80%89%E6%8B%A9%E5%88%87%E7%82%B9%E7%9A%84%E6%B3%A8%E8%A7%A3)
      - [(3) 复用切点表达式](#3-%E5%A4%8D%E7%94%A8%E5%88%87%E7%82%B9%E8%A1%A8%E8%BE%BE%E5%BC%8F)
      - [(4) 环绕通知](#4-%E7%8E%AF%E7%BB%95%E9%80%9A%E7%9F%A5)
      - [(5) 定义切面的类当做普通POJO来使用](#5-%E5%AE%9A%E4%B9%89%E5%88%87%E9%9D%A2%E7%9A%84%E7%B1%BB%E5%BD%93%E5%81%9A%E6%99%AE%E9%80%9Apojo%E6%9D%A5%E4%BD%BF%E7%94%A8)
    - [2.3 开启AOP](#23-%E5%BC%80%E5%90%AFaop)
    - [2.4 向切面传递参数](#24-%E5%90%91%E5%88%87%E9%9D%A2%E4%BC%A0%E9%80%92%E5%8F%82%E6%95%B0)
      - [(1) 传参指示器](#1-%E4%BC%A0%E5%8F%82%E6%8C%87%E7%A4%BA%E5%99%A8)
      - [(2) 切面定义](#2-%E5%88%87%E9%9D%A2%E5%AE%9A%E4%B9%89)
      - [(3) `@Configuration`类配置](#3-configuration%E7%B1%BB%E9%85%8D%E7%BD%AE)
      - [(4) 测试](#4-%E6%B5%8B%E8%AF%95)
    - [2.5 AOP应用例子：使用AOP注解为类添加新功能](#25-aop%E5%BA%94%E7%94%A8%E4%BE%8B%E5%AD%90%E4%BD%BF%E7%94%A8aop%E6%B3%A8%E8%A7%A3%E4%B8%BA%E7%B1%BB%E6%B7%BB%E5%8A%A0%E6%96%B0%E5%8A%9F%E8%83%BD)
    - [2.6 不足](#26-%E4%B8%8D%E8%B6%B3)
  - [3. 使用XML定义切面](#3-%E4%BD%BF%E7%94%A8xml%E5%AE%9A%E4%B9%89%E5%88%87%E9%9D%A2)
    - [3.1 基于XML的AOP配置元素](#31-%E5%9F%BA%E4%BA%8Exml%E7%9A%84aop%E9%85%8D%E7%BD%AE%E5%85%83%E7%B4%A0)
    - [3.2 例子](#32-%E4%BE%8B%E5%AD%90)
      - [(1) 定义通知类、并与切点绑定](#1-%E5%AE%9A%E4%B9%89%E9%80%9A%E7%9F%A5%E7%B1%BB%E5%B9%B6%E4%B8%8E%E5%88%87%E7%82%B9%E7%BB%91%E5%AE%9A)
      - [(2) 复用切点表达式](#2-%E5%A4%8D%E7%94%A8%E5%88%87%E7%82%B9%E8%A1%A8%E8%BE%BE%E5%BC%8F)
      - [(3) 环绕通知](#3-%E7%8E%AF%E7%BB%95%E9%80%9A%E7%9F%A5)
      - [(4) 传递参数](#4-%E4%BC%A0%E9%80%92%E5%8F%82%E6%95%B0)
      - [(5) 用切面引入新功能](#5-%E7%94%A8%E5%88%87%E9%9D%A2%E5%BC%95%E5%85%A5%E6%96%B0%E5%8A%9F%E8%83%BD)
  - [4. 注入AspectJ驱动的切面](#4-%E6%B3%A8%E5%85%A5aspectj%E9%A9%B1%E5%8A%A8%E7%9A%84%E5%88%87%E9%9D%A2)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Spring AOP

## 1. 基础原理

### 1.1 AOP原理

> * 用来让横切面与业务解耦，应用场景如日志、声明式事务、安全、缓存等
> * 面向切片的编程，虽然仍然在一个地方定义通用功能（如日志），但在何处用何种方式使用这些功能，则是用声明的方式（而不是修改业务代码的方式）来定义的、不会侵入到业务代码

### 1.2 Spring AOP

#### (1) 和传统基于代理的AOP对比

> | 特性              | 说明                                                         |
> | ----------------- | ------------------------------------------------------------ |
> | 纯POJO切面        | 使用`POJO + XML`配置，POJO只是提供满足切点时所要调用的方法   |
> | @AspectJ注解切面  | 并不是AspectJ、只是借鉴了AspectJ以提供基于注解的界面。其编程模型与AspectJ完全一致，但让然是基于代理的切面，因此切点也就只能局限在方法上。 |
> | 注入式AspectJ切面 | Spring基于代理注解搞不定的事情，如构造器或属性拦截，将通知注入到的真正的Aspect驱动的切面中 |

#### (2) 对比Spring AOP和AspectJ

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/sprint_aop_vs_aspectj.jpg" width="1024" /></div>

## 2 使用`@AspectJ`注解定义Spring AOP切面

### 2.1 编写切入点

#### (1) 方法

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_aoppointer_1.jpg" width="1024" /></div>

#### (2) 指示器

> 上面的例子用了各种指示器，以限定切点的范围，更多的指示器见下表
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_aoppointer2.jpg" width="1024" /></div>
>
> 只有execution()用来指示切点落在哪些方法上，其他指示器都是用来做限定的
>
> 如果使用了Spring不支持的指示器，会抛IllegalArgument-Exception异常

### 2.2 编写切面

#### (1) 方法

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/sprint_aop_aspect_define_1.jpg" width="1024" /></div>

#### (2) 用于选择切点的注解

> 用于在连接点中选择切点的各种注解                                 
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_aspect_define_2.jpg" width="1024" /></div>
>
> @After在return和抛异常时都执行，而@AfterReturning, @AfterThrowing只在一种情况下执行

#### (3) 复用切点表达式

> 用@Pointcut定义切点，以便复用切点表达式（通过方法名做了个中转），前面例子重写如下
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_aspect_define_3b.jpg" width="1024" /></div>

#### (4) 环绕通知

> 再次重写上面的类，用环绕通知将多个方法合并起来（注意一定要调用jd.proceed()，否则切点位置的方法不会被调用，除非是故意为之）
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_aspect_define_4_around.jpg" width="1024" /></div>
>
> 环绕通知的另一个优势是：可以对同一个调用，在调用前、调用后共享数据，实现诸如计时之类的功能

#### (5) 定义切面的类当做普通POJO来使用

> 上面实现新功能并切入到其他bean的Audience类，除了加了一些注解以外，仍然是POJO，这意味着可以把Audience类装配成bean、添加单元测试等：任何可以对POJO做的事情、都可以用在Audience上
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_aspect_define_5_aspojo.jpg" width="1024" /></div>

### 2.3 开启AOP

光定义切点还不够，要开启AspectJ才能完成切面

> ![image-20210303160638558](/Users/fangkun/Library/Application Support/typora-user-images/image-20210303160638558.jpg" width="1024" /></div>

说明

> 1. Spring AOP仅仅使用@AspectJ作为创建切面的指令，但创建出来的切面依然是基于Spring代理的切面，并不是AspectJ的切面，也不包含AspectJ的所有功能
> 2. AspectJ 5开始支持注解，不再需要学些AspectJ DSL，而Spring AOP只使用了AspectJ的一部分注解，其他注解并没有被使用

### 2.4 向切面传递参数

> 在切点表达式中定义好：(1) 参数类型；(2)参数名称（用`&&args(paraName)`标记；现在可以传递参数给切面了，例子如下

#### (1) 传参指示器

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_params_1_indicator.jpg" width="1024" /></div>

#### (2) 切面定义

> 切点表达式中的args指示器，表示它要把CompactDisc.playTrack方法的trackNumber参数传入
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_parmas_2_aspect_define.jpg" width="1024" /></div>
>
> 切面本身也是个POJO，可添加成员trackCounts:Map<Integer,Integer>

#### (3) `@Configuration`类配置

> 在Configuration类中开启了AspectJ自动代理，同时把切面（TrackCounter）、使用AOP功能的类（CompactDisc）都声明成Bean
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_param_3_config.jpg" width="1024" /></div>

#### (4) 测试

> 注入CompactDisc对象，调用playTrack方法，看切面是否正常工作
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_params_test.jpg" width="1024" /></div>
>
> 切面DiscCounter本身也是POJO，可以注入进来，因此可以调用其方法获得counter的值，以检查计数是否正确

### 2.5 AOP应用例子：使用AOP注解为类添加新功能

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_example.jpg" width="1024" /></div>

### 2.6 不足

> 通知（定义切面的类）必须添加注解，因此必须能够修改这些类的源代码
>
> 如果没有源代码，可以考虑使用接下来介绍的`使用XML生成切面`

## 3. 使用XML定义切面

> 优先使用基于注解的配置，其次使用基于@Configure类的配置，如果前者不能满足需求，则回退到基于XML的配置

### 3.1 基于XML的AOP配置元素

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_xml_cfg_elems.jpg" width="1024" /></div>

### 3.2 例子

#### (1) 定义通知类、并与切点绑定

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_xml_example_aspect_define.jpg" width="1024" /></div>
>
> 备注：`<aop:config>` 标签内可以声明多个切面，每个切面用一个`<aop:aspect>`表示

#### (2) 复用切点表达式

> 与使用AspectJ注解一样，用XML定义的切面同样可以复用切点表达式
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_xml_reuse_expr.jpg" width="1024" /></div>
>
> 备注：定义好的`<aop:pointcut>`可以被哪些aop通知元素引用，取决于`<aop:pointcut>`被定义的位置（`<aop:config>`中还是`<aop:before>`等通知元素中)

#### (3) 环绕通知

> 与使用AspectJ注解一样，用XML也可以定义环绕通知
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_xml_around_notice.jpg" width="1024" /></div>

#### (4) 传递参数

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_xml_params.jpg" width="1024" /></div>
>
> 上面的代码共做了如下3件事情：
>
> 1. 声明了TrackCounter和BlankDisc两个bean
> 2. 将TrackCounter转成了切面
> 3. 在切点表达式中，声明了参数传递方法（包括参数名和参数类型），这个表达式与之前@AspectJ注解中使用的表达式基本相同，唯一的差别是&&被替换成了and，因为XML中&是声明实体的符号

#### (5) 用切面引入新功能

> 与使用AspectJ注解一样，用XML也可以用aop的方式为一个接口，引入来自另一个接口的新功能，增加新的方法
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_xml_intro_new_feature.jpg" width="1024" /></div>

## 4. 注入AspectJ驱动的切面

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_use_aspectj.jpg" width="1024" /></div>