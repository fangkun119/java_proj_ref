[TOC]

# Spring笔记

## 1. Spring IOC 

### (1) IOC基础装配

> 资料来源：Spring In Action第4版第2章
>
> 笔记链接:
>
> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/01_spring_ioc_basic.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/01_spring_ioc_basic.md)

内容介绍

1 . `隐式自动装配`、`@Configure类`、`XML配置`三种装配方式

2 . 三种装配方式混合使用

`@Configure`类中使用`@Import`注解引入另一个`@Configure`类中的Bean

`@Configure`类中使用`@ImportResource`注解引入定义在XML文件中的Bean

`XML`配置中通过`<import>`导入其他XML配置文件中的Bean

### (2) IOC高级装配

> 资料来源：Spring In Action第4版第3章
>
> 笔记链接：
>
> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/02_spring_ioc_advanced.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/02_spring_ioc_advanced.md)

主要内容

1 . Profile的使用：为不同的环境创建不同的Bean

2 . 条件化Bean：根据程序计算结果决定是否创建某个Bean

3 . 处理自动装配歧义：`Primary`、`Qualifier`以及Bean特征描述注解

4 . Bean生命周期

5 . 注入运行期的值：`属性占位符`以及使用`SPEL表达式`

技巧介绍

1 .  Session Bean注入到Singleton Bean时，通过代理来保持其Session生命周期的特性

## 2. Spring AOP

> 资料来源：Spring In Action第4版第4章
>
> 笔记链接:
>
> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/04_spring_aop.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/04_spring_aop.md)

主要内容

1 . AOP原理，对比传统AOP以及AspectJ，Spring AOP的特点

2 . 使用注解定义Spring AOP切面

3 . 使用XML配置定义Spring AOP切面

## 3. Spring 框架

### (1) Spring 框架介绍

> 资料来源：Spring In Action第6版第1章
>
> 笔记链接：
>
> [https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch01_spring_intro.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch01_spring_intro.md)

内容

> 1. Spring介绍、应用创建、项目结构

技巧

> 1. 理解`@SpringBootApplication`，`@SpringBootTest`注解
> 2. 使用`@WebMvcTest`和`MockMvc`编写用于测试Controller的单元测试
> 3. 使用`@SpringBootTest`和`HtmlUnitDriver`编写用于测试视图模板的单元测试

### (2) Spring Boot Web项目

> 资料来源：Spring In Action第6版第2章

笔记链接

> [https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch02_web_app.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch02_web_app.md)

内容

> 1. Spring Boot MVC项目的编写

技巧

> 1. 使用`lambok`简化Domain Object的编写
> 2. 使用`@Slf4j`简化日志打印代码
> 3. 使用`@ModelAttribute`、`Model`注入来管理与前端页面的数据交互
> 4. 使用`@SessionAttributes`设置Model中属性的生命周期为Session
> 5. 使用`Spring Validation API`进行表单参数校验
> 6. 使用`ViewControllerRegistry`简化Controller的代码编写
> 7. 在开发环境中禁用模板引擎缓存、以便于视图模板调试

### (3) 访问关系型数据库

> 资料来源：Spring In Action第6版第3章

笔记链接

> [https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch03_spring_data.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch03_spring_data.md)

内容

> 1. `JDBC Template`
>     * 将执行sql以外的操作封装在Template中
> 2. `Spring Data JDBC`
>     * 使用CrudRepository封装的常用DB操作
>     * 通过`@Query`注解（只能传入SQL）编写自定义方法
> 3. `Spring Data JPA`
>     * 使用CrudRepository封装的常用操作
>     * 使用方法签名推断声明自定义方法
>     * 通过`@Query`注解（可以传入JPA Query和Native SQL）编写自定义方法

技巧

> 1. 使用H2内存数据库作为开发环境
> 2. 程序启动自动执行sql脚本
> 3. 使用`JDBC TEmplate`的封装类`SimpleJdbcInsert`来减轻Insert嵌套Domain Objects时代码冗余的问题
> 4. 使用`Spring Data JDBC`/`Spring Data JPA`时，框架可以自动生成用于Insert嵌套Object的SQL，不需要额外代码
> 5. 程序启动时自动执行操作：`CommandLineRunner`或`Application Runner`

### (4) 访问NoSQL数据库

> 资料来源： Spring In Action第6版第4章

笔记：[https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch04_non_relational_data.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch04_non_relational_data.md)

内容

> 1. `Spring Data MongoDB`
>
> 2. `Spring Data Cassandra`（跳过）

### (5) 用户注册和登录认证

> 资料来源： Spring In Action第6版第5章

笔记链接

> [https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch05_security.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch05_security.md)

内容

> 1. `PasswordEncoder`，`UserDetailsService`，`SecurityFilterChain`三个Bean的配置
>     * `PasswordEncoder`：密码明文加密编码器的选择
>     * `UserDetailsService`：用户信息的存储、查询
>     * `SecurityFilterChain`：访问规则，登录页，登出页，跨域豁免配置
>
> 2. 用户注册页，登录页及登录功能编写
>
> 3. 使用`spring-boot-starter-oauth2-client`实现第三方认证

技巧

> 1. 理解引入`spring-boot-starter-security`之后Spring所完成的自动配置
> 2. 使用SpEL表达式自定义访问规则
> 3. 使用CSRF Token防止跨域攻击
> 4. 细粒度访问规则配置
>     * 在SecurityFilterChain中为不同路径不同权限
>     * 使用@PreAuthorize为不同方法设置不同权限
>     * 使用@PostAuthorize根据方法返回结果设置不同权限
> 5. 使用`@AuthenticationPrincipal`让当前用户的信息注入到方法中，以便使用用户信息

### (6) Configuration Properties

> 资料来源：Spring In Action第6版第6章

笔记链接

> [https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch06_configuration.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch06_configuration.md)

内容

> 1. `Configuration Properties`的作用：聚合多个配置数据源、提供一站式配置服务
>
>     具体包括：
>
>     * JVM系统属性
>     * 操作系统环境变量
>     * 命令行参数
>     * 配置文件：`application.properties`、`application.yml`、……
>     * 微服务中的`config server`
>     * ……
>
> 2. 编写自定义`Configuration Properties`并且在代码中使用
>
> 3. 使用spring profile来为不同的运行环境编写不同的配置

技巧

> 1. Spring框架内置的`Configuration Properties`使用举例
>
>     (1) DataSource：`spring.datasource`
>
>     (2) 数据库建库及初始化脚本：`spring.datasource.schema`，`spring.datasource.data`
>
>     (3) 随机端口的使用：`server.port = 0`
>
>     (4) 让程序支持HTTPS
>
>     (5) Spring日志打印细节配置
>
>     (6) 在配置文件引用其他配置项 
>
> 2. 使用Configuration Properties元数据声明来关闭某些IDE的"unknown property"报警
>
> 3. 激活多个profile环境
>
> 4. 使用profile条件化地创建Bean

## 4 Spring 集成

### (1) REST Service

> 资料来源：Spring In Action第6版第7章

笔记链接：[https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch07_rest_services.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch07_rest_services.md)

内容

> 1. Restful Controller的编写
> 2. HATEOAS（超媒体应用状态引擎的使用）
> 3. 使用Spring Data REST自动生成REST API Endpoint

技巧

> 1. PUT和PATCH语义表达的区别

### (2) REST Client

> 资料来源：Spring In Action第6版第8章

笔记链接：[https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch08_rest_client.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch08_rest_client.md)

内容

> 1. 使用RestTemplate来访问REST API
> 2. 使用Traverson来根据HATEOAS返回的API Relation进行导航

### (3) Securing REST Security

> Spring In Action第6版第9章尚未出版（全书预计出版日期2021.07）
>
> 以下是相关内容《Spring Microservice In Action 第2版第9章：身份验证及访问管理》的笔记

笔记链接：[https://github.com/fangkun119/manning-smia/blob/master/note/ch09_security.md](https://github.com/fangkun119/manning-smia/blob/master/note/ch09_security.md)

> 内容
>
> 1. 用开源的KeyCloak搭建身份验证服务器，并在微服务调用链中传播Access Token
> 2. 使用Access Token验证REST调用是否合法

### (4) 异步消息收发

> 资料来源：Spring In Action第5版第8章（对应尚未出版的Spring In Action第6版第10章）

笔记链接

> [https://github.com/fangkun119/spring-in-action-5-samples/blob/master/note/ch08_async_msg.md](https://github.com/fangkun119/spring-in-action-5-samples/blob/master/note/ch08_async_msg.md)

内容

> 1. 使用`JMS + ActiveMQ Artemis / Legacy AcrtiveMQ`作为消息队列
> 2. 使用`AMQP + RabbitMQ`作为消息队列
> 3. 使用`Spring Kafaka + Kafka`作为消息队列

相关笔记

> 1. Kafka: [../300_kafka/readme.md](../300_kafka/readme.md)
> 2. Spring Kafka Demos：[../300_kafka/07_advanced_topics/readme.md](../300_kafka/07_advanced_topics/readme.md)

### (5) Spring Integration

> 资料来源：Spring In Action第5版第9章（对应尚未出版的Spring In Action第6版第11章）

笔记链接

> [https://github.com/fangkun119/spring-in-action-5-samples/blob/master/note/ch09_spring_integ.md](https://github.com/fangkun119/spring-in-action-5-samples/blob/master/note/ch09_spring_integ.md)

内容

> (1) Spring Integration Flow用于连接外部系统的小Demo演示，`XML`/`Java`/`DSL`三种配置方式
>
> (2) Spring Integration Flow九个组件的功能和示例
>
> (3) Demo项目：轮询Email收件箱→从邮件中解析订单对象→自动调用REST API提交订单

### 3. 其他

Reactive  Spring（CH12-15：Reactor，RSocket，Reactively）

> 笔记尚未完成，包含4章：
>
> CH12 Introducing Reactor：对应[第5版CH10](https://livebook.manning.com/book/spring-in-action-fifth-edition/chapter-10?origin=product-toc)
>
> CH13 Developing Reactive API：对应[第5版CH11](https://livebook.manning.com/book/spring-in-action-fifth-edition/chapter-11?origin=product-toc)
>
> CH14 Working with RSocket：第5版无对应章节
>
> CH15 Persisting Data Reactively：对应[第5版CH12](https://livebook.manning.com/book/spring-in-action-fifth-edition/chapter-12?origin=product-toc)

Deploying Spring（CH16-19：Actuator，JMX，Administerning，Deploying）

> 笔记尚未完成，包含4章：
>
> CH16 Spring Boot Actuator：对应[第5版CH16](https://livebook.manning.com/book/spring-in-action-fifth-edition/chapter-16?origin=product-toc)
>
> CH17 Admnistering Spring：对应[第5版CH17](https://livebook.manning.com/book/spring-in-action-fifth-edition/chapter-17?origin=product-toc)
>
> CH18 Monitoring Spring with JMX：对应[第5版CH18](https://livebook.manning.com/book/spring-in-action-fifth-edition/chapter-17?origin=product-toc)
>
> CH19 Delpoying Spring：对应[第5版CH19](https://livebook.manning.com/book/spring-in-action-fifth-edition/chapter-19?origin=product-toc)



### (3) IOC Demo

> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/03_spring_ioc_demos.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/03_spring_ioc_demos.md)

内容

> 1. Bean装配Demo，Bean声明周期
> 2. Bean循环依赖问题

## 

