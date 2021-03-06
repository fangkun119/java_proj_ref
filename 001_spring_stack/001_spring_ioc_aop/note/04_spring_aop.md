<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Spring AOP](#spring-aop)
  - [1. 基础原理](#1-%E5%9F%BA%E7%A1%80%E5%8E%9F%E7%90%86)
    - [1.1 AOP原理](#11-aop%E5%8E%9F%E7%90%86)
      - [(1) AOP](#1-aop)
      - [(2) AOP术语](#2-aop%E6%9C%AF%E8%AF%AD)
    - [1.2 Spring AOP](#12-spring-aop)
      - [(2) 四种AOP配置方案](#2-%E5%9B%9B%E7%A7%8Daop%E9%85%8D%E7%BD%AE%E6%96%B9%E6%A1%88)
      - [(3) 与AspectJ对比](#3-%E4%B8%8Easpectj%E5%AF%B9%E6%AF%94)
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

#### (1) AOP

> * 用来让横切面与业务解耦，应用场景如日志、声明式事务、安全、缓存等
> * 面向切片的编程，虽然仍然在一个地方定义通用功能（如日志），但在何处用何种方式使用这些功能，则是用声明的方式（而不是修改业务代码的方式）来定义的、不会侵入到业务代码

#### (2) AOP术语

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/001_spring_aop_terminology.jpg" width="300" /></div>
>
> | 属于                  | 用途                                                         |
> | --------------------- | ------------------------------------------------------------ |
> | 通知  advice          | 定义切面`切什么`及`何时`执行，包括：before、after、after-returning、after-throwing、around |
> | 连接点  joining-point | 能够`插入切面的点（时机）`，例如：方法调用、异常抛出、修改字段 …… |
> | 切点     cut-point    | `选中的连接点`，用来织入切面，定义切点的方法包括：类名方法名、正则表达式、运行时动态选择 |
> | 切面  aspect          | 将advice切入到cut-point，并设定执行的操作，这样就定义好了一个切面(aspect) |
> | 引入  introduce       | 不修改某个类，而是用切面让某个类具有新的方法（行为）、属性（状态）：例如切入一个Logger |
> | 织入  waving          | 把切面织入到目标对象的过程，有以下三种：（1）编译期织入：借助特殊编译器，如AspectJ织入编译器 （2）加载期织入：借助特殊ClassLoader，如AspectJ5的LTW （3） 运行期织入：借助容器创建动态代理，如Spring AOP |
>
> 总结一下：
>
> 1. AOP：通过创建`切面（aspect）`，以不修改目标类的方式将附加的功能`引入（introduce）`到一个类中
>
> 2. AOP完成这个任务的过程叫做`织入（waving）`，Spring AOP采用的实现方式是通过`动态代理`在`运行期`织入
>
> 3. 创建`切面`需要进行配置，包括如下方面：
>
>     (1) 确定`切点（cut-point）`：通过"类名方法名"/"正则表达式"/”运行时动态选择”等方式，在方法调用、异常抛出、字段修改等各种备选的`连接点`中进行选择
>
>     (2) 在切点上设置一个`通知（advice）`：在这个连接点上执行什么操作（即切面中所定义的方法）以及何时执行（包括before、after、after-returning、after-throwing、around）
>
> 关于Spring AOP实现时所用到的动态代理，参考：[05_java_proxy.md](05_java_proxy.md)

### 1.2 Spring AOP

#### (2) 四种AOP配置方案

> | 特性              | 说明                                                         |
> | ----------------- | ------------------------------------------------------------ |
> | Spring经典AOP     | 老式的AOP编写方法，需要编写ProxyFactory Bean等，笨重复杂，以及被下面的`纯POJO切面`和`@AspectJ注解切面`所替代 |
> | 纯POJO切面        | 对经典AOP的封装，使用`POJO + XML`配置，POJO只需提供满足切点时所要调用的方法 |
> | @AspectJ注解切面  | 对经典AOP的封装，使用`POJO + @AspectJ`注解。它并不是真的AspectJ，只是借鉴了AspectJ提供的注解并遵循相同的编码方式。但是底层仍然是Spring经典AOP，通过动态代理实现，因此切点仅仅局限在方法上。 |
> | 注入式AspectJ切面 | 真正的AspectJ切面，用来解决Spring AOP搞不定的事情。例如如构造器或属性拦截等。 |

#### (3) 与AspectJ对比

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

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/001_spring_aop/spring_aop_enable_aop.jpg" width="1024" /></div>

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

