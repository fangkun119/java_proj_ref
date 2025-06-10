<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Spring笔记](#spring%E7%AC%94%E8%AE%B0)
  - [1. Spring IOC](#1-spring-ioc)
    - [(1) IOC基础装配](#1-ioc%E5%9F%BA%E7%A1%80%E8%A3%85%E9%85%8D)
    - [(2) IOC高级装配](#2-ioc%E9%AB%98%E7%BA%A7%E8%A3%85%E9%85%8D)
  - [2. Spring AOP](#2-spring-aop)
  - [3. Spring 框架](#3-spring-%E6%A1%86%E6%9E%B6)
    - [(1) Spring 框架介绍](#1-spring-%E6%A1%86%E6%9E%B6%E4%BB%8B%E7%BB%8D)
    - [(2) Spring Boot Web项目](#2-spring-boot-web%E9%A1%B9%E7%9B%AE)
    - [(3) 访问关系型数据库](#3-%E8%AE%BF%E9%97%AE%E5%85%B3%E7%B3%BB%E5%9E%8B%E6%95%B0%E6%8D%AE%E5%BA%93)
    - [(4) 访问NoSQL数据库](#4-%E8%AE%BF%E9%97%AEnosql%E6%95%B0%E6%8D%AE%E5%BA%93)
    - [(5) 用户注册和登录认证](#5-%E7%94%A8%E6%88%B7%E6%B3%A8%E5%86%8C%E5%92%8C%E7%99%BB%E5%BD%95%E8%AE%A4%E8%AF%81)
    - [(6) Configuration Properties](#6-configuration-properties)
  - [4 Spring 集成](#4-spring-%E9%9B%86%E6%88%90)
    - [(1) REST Service](#1-rest-service)
    - [(2) REST Client](#2-rest-client)
    - [(3) Securing REST Security](#3-securing-rest-security)
    - [(4) 异步消息收发](#4-%E5%BC%82%E6%AD%A5%E6%B6%88%E6%81%AF%E6%94%B6%E5%8F%91)
    - [(5) Spring Integration](#5-spring-integration)
  - [5. Spring Batch](#5-spring-batch)
    - [(1&2) 框架结构、执行过程、任务状态存储](#12-%E6%A1%86%E6%9E%B6%E7%BB%93%E6%9E%84%E6%89%A7%E8%A1%8C%E8%BF%87%E7%A8%8B%E4%BB%BB%E5%8A%A1%E7%8A%B6%E6%80%81%E5%AD%98%E5%82%A8)
    - [(3) 定义Batch Job的执行流程](#3-%E5%AE%9A%E4%B9%89batch-job%E7%9A%84%E6%89%A7%E8%A1%8C%E6%B5%81%E7%A8%8B)
    - [(4) 输入](#4-%E8%BE%93%E5%85%A5)
    - [(5) 输出](#5-%E8%BE%93%E5%87%BA)
    - [(6) Item转换](#6-item%E8%BD%AC%E6%8D%A2)
    - [(7) 故障处理方法](#7-%E6%95%85%E9%9A%9C%E5%A4%84%E7%90%86%E6%96%B9%E6%B3%95)
  - [6. 其他](#6-%E5%85%B6%E4%BB%96)
    - [(1) Spring IOC Demo](#1-spring-ioc-demo)
    - [(2) Spring Boot Demo 1](#2-spring-boot-demo-1)
    - [(4) Spring Boot Demo 2](#4-spring-boot-demo-2)
    - [(3) Thymeleaf参考文档](#3-thymeleaf%E5%8F%82%E8%80%83%E6%96%87%E6%A1%A3)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Spring笔记

## 1. Spring IOC 

### (1) IOC基础装配

> 笔记链接（资料来源：Spring In Action第4版第2章）
>
> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/01_spring_ioc_basic.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/01_spring_ioc_basic.md)

主要内容

> 1. `隐式自动装配`、`@Configure类`、`XML配置`三种装配方式
>
> 2.  三种装配方式混合使用
>
>     `@Configure`类中使用`@Import`注解引入另一个`@Configure`类中的Bean
>
>     `@Configure`类中使用`@ImportResource`注解引入定义在XML文件中的Bean
>
>     `XML`配置中通过`<import>`导入其他XML配置文件中的Bean

### (2) IOC高级装配

> 笔记链接（资料来源：Spring In Action第4版第3章）
>
> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/02_spring_ioc_advanced.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/02_spring_ioc_advanced.md)

主要内容

> 1. Profile的使用：为不同的环境创建不同的Bean
> 2. 条件化Bean：根据程序计算结果决定是否创建某个Bean
> 3. 处理自动装配歧义：`Primary`、`Qualifier`以及Bean特征描述注解
> 4. Bean生命周期
> 5. 注入运行期的值：`属性占位符`以及使用`SPEL表达式`

技巧介绍

> 1. Session Bean注入到Singleton Bean时，通过代理来保持其Session生命周期的特性

## 2. Spring AOP

> 笔记链接（资料来源：Spring In Action第4版第4章）
>
> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/04_spring_aop.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/04_spring_aop.md)

主要内容

> 1. AOP原理，对比传统AOP以及AspectJ，Spring AOP的特点
> 2. 使用注解定义Spring AOP切面
> 3. 使用XML配置定义Spring AOP切面

## 3. Spring 框架

### (1) Spring 框架介绍

> 笔记链接（资料来源：Spring In Action第6版第1章）：
>
> [https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch01_spring_intro.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch01_spring_intro.md)

主要内容

> 1. Spring介绍、应用创建、项目结构

技巧介绍

> 1. 理解`@SpringBootApplication`，`@SpringBootTest`注解
> 2. 使用`@WebMvcTest`和`MockMvc`编写用于测试Controller的单元测试
> 3. 使用`@SpringBootTest`和`HtmlUnitDriver`编写用于测试视图模板的单元测试

### (2) Spring Boot Web项目

> 笔记链接（资料来源：Spring In Action第6版第2章）
>
> [https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch02_web_app.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch02_web_app.md)

主要内容

> 1. Spring Boot MVC项目的编写

技巧介绍

> 1. 使用`lambok`简化Domain Object的编写
> 2. 使用`@Slf4j`简化日志打印代码
> 3. 使用`@ModelAttribute`、`Model`注入来管理与前端页面的数据交互
> 4. 使用`@SessionAttributes`设置Model中属性的生命周期为Session
> 5. 使用`Spring Validation API`进行表单参数校验
> 6. 使用`ViewControllerRegistry`简化Controller的代码编写
> 7. 在开发环境中禁用模板引擎缓存、以便于视图模板调试

### (3) 访问关系型数据库

> 笔记链接（资料来源：Spring In Action第6版第3章）
>
> [https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch03_spring_data.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch03_spring_data.md)

主要内容

> 1. `JDBC Template`
>     * 将执行sql以外的操作封装在Template中
> 2. `Spring Data JDBC`
>     * 使用CrudRepository封装的常用DB操作
>     * 通过`@Query`注解（只能传入SQL）编写自定义方法
> 3. `Spring Data JPA`
>     * 使用CrudRepository封装的常用操作
>     * 使用方法签名推断声明自定义方法
>     * 通过`@Query`注解（可以传入JPA Query和Native SQL）编写自定义方法

技巧介绍

> 1. 使用H2内存数据库作为开发环境
> 2. 程序启动自动执行sql脚本
> 3. 使用`JDBC TEmplate`的封装类`SimpleJdbcInsert`来减轻Insert嵌套Domain Objects时代码冗余的问题
> 4. 使用`Spring Data JDBC`/`Spring Data JPA`时，框架可以自动生成用于Insert嵌套Object的SQL，不需要额外代码
> 5. 程序启动时自动执行操作：`CommandLineRunner`或`Application Runner`

### (4) 访问NoSQL数据库

> 笔记链接（资料来源： Spring In Action第6版第4章）
>
> [https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch04_non_relational_data.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch04_non_relational_data.md)

主要内容

> 1. `Spring Data MongoDB`
>
> 2. `Spring Data Cassandra`（跳过）

### (5) 用户注册和登录认证

> 笔记链接（资料来源： Spring In Action第6版第5章）
>
> [https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch05_security.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch05_security.md)

主要内容

> 1. `PasswordEncoder`，`UserDetailsService`，`SecurityFilterChain`三个Bean的配置
>     * `PasswordEncoder`：密码明文加密编码器的选择
>     * `UserDetailsService`：用户信息的存储、查询
>     * `SecurityFilterChain`：访问规则，登录页，登出页，跨域豁免配置
>
> 2. 用户注册页，登录页及登录功能编写
>
> 3. 使用`spring-boot-starter-oauth2-client`实现第三方认证

技巧介绍

> 1. 理解引入`spring-boot-starter-security`之后Spring所完成的自动配置
> 2. 使用SpEL表达式自定义访问规则
> 3. 使用CSRF Token防止跨域攻击
> 4. 细粒度访问规则配置
>     * 在SecurityFilterChain中为不同路径不同权限
>     * 使用@PreAuthorize为不同方法设置不同权限
>     * 使用@PostAuthorize根据方法返回结果设置不同权限
> 5. 使用`@AuthenticationPrincipal`让当前用户的信息注入到方法中，以便使用用户信息

### (6) Configuration Properties

> 笔记链接（资料来源：Spring In Action第6版第6章）
>
> [https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch06_configuration.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch06_configuration.md)

主要内容

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

技巧介绍

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

> 笔记链接（资料来源：Spring In Action第6版第7章）
>
> [https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch07_rest_services.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch07_rest_services.md)

主要内容

> 1. Restful Controller的编写
> 2. HATEOAS（超媒体应用状态引擎的使用）
> 3. 使用Spring Data REST自动生成REST API Endpoint

技巧介绍

> 1. PUT和PATCH语义表达的区别

### (2) REST Client

> 笔记链接（资料来源：Spring In Action第6版第8章）
>
> [https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch08_rest_client.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch08_rest_client.md)

主要内容

> 1. 使用RestTemplate来访问REST API
> 2. 使用Traverson来根据HATEOAS返回的API Relation进行导航

### (3) Securing REST Security

笔记链接1（资料来源：Spring In Action第6版第9章 Rest Security）

> [https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch09_rest_security.md](https://github.com/fangkun119/spring-in-action-6-samples/blob/main/note/ch09_rest_security.md)

笔记链接2（资料来源：Spring Microservice In Action 第2版第9章：身份验证及访问管理）

> [https://github.com/fangkun119/manning-smia/blob/master/note/ch09_security.md](https://github.com/fangkun119/manning-smia/blob/master/note/ch09_security.md)

主要内容

> 1. 用开源的KeyCloak搭建身份验证服务器，并在微服务调用链中传播Access Token
> 2. 使用Access Token验证REST调用是否合法

### (4) 异步消息收发

> 笔记链接（资料来源：Spring In Action第5版第8章，对应尚未出版的Spring In Action第6版第10章）
>
> [https://github.com/fangkun119/spring-in-action-5-samples/blob/master/note/ch08_async_msg.md](https://github.com/fangkun119/spring-in-action-5-samples/blob/master/note/ch08_async_msg.md)

主要内容

> 1. 使用`JMS + ActiveMQ Artemis / Legacy AcrtiveMQ`作为消息队列
> 2. 使用`AMQP + RabbitMQ`作为消息队列
> 3. 使用`Spring Kafaka + Kafka`作为消息队列

相关笔记

> 1. Kafka: [../300_kafka/readme.md](../300_kafka/readme.md)
> 2. Spring Kafka Demos：[../300_kafka/07_advanced_topics/readme.md](../300_kafka/07_advanced_topics/readme.md)

### (5) Spring Integration

> 笔记链接（资料来源：Spring In Action第5版第9章，对应尚未出版的Spring In Action第6版第11章）
>
> [https://github.com/fangkun119/spring-in-action-5-samples/blob/master/note/ch09_spring_integ.md](https://github.com/fangkun119/spring-in-action-5-samples/blob/master/note/ch09_spring_integ.md)

主要内容

> (1) Spring Integration Flow用于连接外部系统的小Demo演示，`XML`/`Java`/`DSL`三种配置方式
>
> (2) Spring Integration Flow九个组件的功能和示例
>
> (3) Demo项目：轮询Email收件箱→从邮件中解析订单对象→自动调用REST API提交订单

## 5. Spring Batch

> 资料来源：[O‘Reilly Learning Spring Batch](http://shop.oreilly.com/product/0636920044673.do)

### (1&2) 框架结构、执行过程、任务状态存储

> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/004_spring_batch/learning_spring_batch/01_02_conception.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/004_spring_batch/learning_spring_batch/01_02_conception.md)

主要内容

> 1. 项目创建
>
> 2. JSR-352 Batch框架结构以及各模块介绍
>
>     Tasklet与Chunk Based Step两种Step的执行过程
>
> 3. JobRepository
>
>     用于开发环境的Memory Based Repository，用于生产环境的JDBC Repository
>
>     JobRepository数据库表结构、用途、Job故障重启时所执行的操作
>
>     Job、Job Instance、Job Instance Execution层级结构

### (3) 定义Batch Job的执行流程

> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/004_spring_batch/learning_spring_batch/03_job_flow.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/004_spring_batch/learning_spring_batch/03_job_flow.md)

主要内容：定义Spring Batch步骤之间的依赖跳转关系

> 1. Transactions：用串行依赖，或者有向图依赖关系定义Step之间的跳转关系
> 2. Flows：将一组Step间的依赖关系抽象为一个Flow，增强代码复用和对复杂业务的支持
> 3. Splits：让两个Flow并发执行
> 4. Decisions：在Step依赖关系中加入判断节点
> 5. Nested Jobs：在一个Job执行过程中调用另一个Job
> 6. Listeners：在Job Instance生命周期的各个时间节（Job、Step、Chunk、Item Read/Process/Write）点执行预设的回调函数
> 7. Parameters：向Batch Job传递参数

### (4) 输入

> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/004_spring_batch/learning_spring_batch/04_input.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/004_spring_batch/learning_spring_batch/04_input.md)

主要内容：Spring Batch的输入

> 1. `ItemReader<T>`接口，以及ItemReader Bean的组装和使用
> 2. `JdbcPagingItemReader`（读取数据库）/`StaxEventItemReader`（读取XML）/`FlatFileItemReader`（读取CSV文件）
> 3. `MultiResourceItemReader`以代理的方式从多个文件读取数据 / `JsonItemReader`（读取JSON）
> 4. `ItemStreamReader`对读取状态维护，以及在读取数据时向BATCH_STEP_EXECUTION_CONTEXT表写入的状态数据；扩展`ItemStreamReader<T>`的方式编写可以维度读取状态的Reader，以便在job instance异常退出并重启后，可以从state记录的位置继续运行

### (5) 输出

> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/004_spring_batch/learning_spring_batch/05_output.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/004_spring_batch/learning_spring_batch/05_output.md)

主要内容：Spring Batch的输出

> 1. `ItemWriter`接口及ItemWriter Bean的组装和使用
>
> 2. `JdbcBatchItemWriter`（写到数据库）/`FlatFileItemWriter`（写到CSV文件）/ `StaxEventItemWriter`（写到XML文件）
>
> 3. `CompositeItemWriter`
>
>     (1) 数据输出两份拷贝，分别到两个不同类型的输出目的地
>
>     (2) 一部分数据输出到目的地A，一部分输出到目的地B
>
> 4. Item Writer的状态维护

### (6) Item转换

> 笔记链接
>
> https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/004_spring_batch/learning_spring_batch/06_processing.md

主要内容：在Reader/Writer之间，对每个Item做数据加工的Processor

> 1. `ItemProcessor`接口及Processor Bean的组装和使用
>
> 2. 应用例子
>
>     (1) Item挑选（Filter）
>
>     (2) 忽略异常Item（`Validator<T>`）
>
>     (3) 以代理的方式组装多个ItemProcessor（`CompositeItemProcessor`）

### (7) 故障处理方法

> 笔记链接
>
> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/004_spring_batch/learning_spring_batch/07_error_handling.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/004_spring_batch/learning_spring_batch/07_error_handling.md)

主要内容：Job Instance故障失败时的处理方法及Demo演示

> 1. 重启：跳过已经completed step，跳过失败step已经处理完毕的chunk，从发生故障的chunck开始继续执行
>
> 2. 自动重试：在遇到特定类型的Exception（例如网络请求失败）时自动重试
>
> 3. 忽略错误：
>
>     (1) 忽略一定数量抛出指定Excetion的Item
>
>     (2) 设置监听器，当有Item处理错误时，调用监听器的回调函数 

## 6. 其他

### (1) Spring IOC Demo

> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/03_spring_ioc_demos.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/001_spring_ioc_aop/note/03_spring_ioc_demos.md)

内容

> 1. Bean装配Demo，Bean生命周期
> 2. Bean循环依赖问题

### (2) Spring Boot Demo 1

> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/002_spring_boot/note/01_spring_boot_demos.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/002_spring_boot/note/01_spring_boot_demos.md)

内容

> 1. 表单接收
> 2. 后端参数校验
> 3. Spring JPA及常用API
> 4. JSP的使用
> 5. Bootstrap、Thymeleaf的使用
> 6. MyBatis、MyBatis Generator GUI，分页器PageHelper在后端部分的使用
> 7. Spring Boot Web项目常用配置
> 8. 文件上传

### (4) Spring Boot Demo 2

> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/003_spring_webproj_ref/note/01_spring_webprojref_demos.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/003_spring_webproj_ref/note/01_spring_webprojref_demos.md)

内容

> 1. 拦截器、登录、登出功能
> 2. 完整分页器使用：前端bootstrap + thymeleaf；后端MyBatis
> 3. JQuery异步调用
> 4. 项目配置文件
> 5. MyBatis Controller
> 6. REST Controller

### (3) Thymeleaf参考文档

> [https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/002_spring_boot/note/02_thymeleaf.md](https://github.com/fangkun119/java_proj_ref/blob/master/001_spring_stack/002_spring_boot/note/02_thymeleaf.md)

<!--Reactive  Spring：Reactor/RSocket/Reactively-->

<!--Spring In Action第6版 CH12-15：Reactor，RSocket，Reactively-->

<!--CH12 Introducing Reactor：对应[第5版CH10](https://livebook.manning.com/book/spring-in-action-fifth-edition/chapter-10?origin=product-toc)-->

<!--CH13 Developing Reactive API：对应[第5版CH11](https://livebook.manning.com/book/spring-in-action-fifth-edition/chapter-11?origin=product-toc)-->

<!--CH14 Working with RSocket：第5版无对应章节-->

<!--CH15 Persisting Data Reactively：对应[第5版CH12](https://livebook.manning.com/book/spring-in-action-fifth-edition/chapter-12?origin=product-toc)-->

### <!--(2) Deploying Spring：Actuator/JMX/Administerning/Delpoying-->

 <!--Spring In Action第6版  CH16-19：Actuator，JMX，Administerning，Deploying）-->

 <!--CH16 Spring Boot Actuator：对应[第5版CH16](https://livebook.manning.com/book/spring-in-action-fifth-edition/chapter-16?origin=product-toc)-->

 <!--CH17 Admnistering Spring：对应[第5版CH17](https://livebook.manning.com/book/spring-in-action-fifth-edition/chapter-17?origin=product-toc)-->

 <!--CH18 Monitoring Spring with JMX：对应[第5版CH18](https://livebook.manning.com/book/spring-in-action-fifth-edition/chapter-17?origin=product-toc)-->

 <!--CH19 Delpoying Spring：对应[第5版CH19](https://livebook.manning.com/book/spring-in-action-fifth-edition/chapter-19?origin=product-toc)--> 

