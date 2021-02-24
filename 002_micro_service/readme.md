[TOC]

# 笔记及代码阅读

## Spring Microservices in Action, 2nd Edition

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/microservice/smia2_cover.jpg" width="250" /></div>

原书连接：[https://www.manning.com/books/spring-microservices-in-action-second-edition](https://www.manning.com/books/spring-microservices-in-action-second-edition)

在线阅读：[https://livebook.manning.com/book/spring-microservices-in-action-second-edition](https://livebook.manning.com/book/spring-microservices-in-action-second-edition/)

代码fork：[https://github.com/fangkun119/manning-smia](https://github.com/fangkun119/manning-smia)

### CH01：微服务及构建模式

笔记链接：[https://github.com/fangkun119/manning-smia/blob/master/note/ch01_microsvc_and_patterns.md](https://github.com/fangkun119/manning-smia/blob/master/note/ch01_microsvc_and_patterns.md)

内容概要：

> 1. 微服务介绍、涉及：(1) 微服务架构对比传统架构；(2) Spring→Spring Boot→微服务的变迁；(3) 云计算的托管程度以及为微服务提供的支持
> 2. 构建微服务时需要考虑的因素、包括：(1) Right Sized；(2) Location Transaprent；(3) Resilient；(4) Instance Repeatable；(4) Scalable
> 3. 构建微服务时可以遵循的7个模式
>     * Core Development Patterns
>     * Routing Patterns
>     * Client Resiliency Patterns
>     * Security Patterns
>     * Logging and Tracing Patterns
>     * Application Metrics Patterns
>     * Build and Deployment Patterns

### CH02：Spring Cloud及云原生应用实践指南

笔记链接：[https://github.com/fangkun119/manning-smia/blob/master/note/ch02_springcloud_and_12factor.md](https://github.com/fangkun119/manning-smia/blob/master/note/ch02_springcloud_and_12factor.md)

内容概要：

> 1. Spring Cloud介绍、包括：(1) Config (2) Service Discovery (3) Load Balance以及Resilience4j (4) API Gateway (5) Spring Cloud Stream (6) Sleuth (7) Spring Cloud Security
> 2. 云原生应用实践指南（Twelve Factor App）包括：(1) 代码库；(2) 库依赖；(3) 配置管理；(4) 支持服务；(5) 构建/运行/发布流程；(6) 无状态设计；(7) 仅绑定端口；(8) 并发支持；(9) 一次性原则(Disposability)；(10) 环境等价性原则（Dev/Prod Parity）；(11) 日志处理；(12) 维护工作脚本化

### CH03：Spring Boot在微服务中的使用

笔记链接：[https://github.com/fangkun119/manning-smia/blob/master/note/ch03_build_service_with_springboot.md](https://github.com/fangkun119/manning-smia/blob/master/note/ch03_build_service_with_springboot.md)

内容概要：

> 1. 设计：将业务问题拆解成微服务的分析方法和注意事项
> 2. 开发：使用Spring Boot开发服务、国际化支持、以及使用HATEOAS返回相关API链接
> 3. DevOPs：微服务部署的原则和步骤，使用Spring Actuator创建健康检查端点

### CH04：Docker在微服务中的使用

笔记链接：[https://github.com/fangkun119/manning-smia/blob/master/note/ch04_docker.md](https://github.com/fangkun119/manning-smia/blob/master/note/ch04_docker.md)

内容概要：

> 1. Docker介绍及Dockerfile编写
>
> 2. docker-compose：单宿主机容器编排工具
>
> 3. 在SpringBoot项目中使用Docker
>
>     (1) 普通构建（basic Dockerfile）与多级构建（Multi-stage Dockerfile）
>
>     (2) 使用Buildpacks工具优化Docker镜像分层
>
>     (3) 使用LAYERED_JAR优化Docker镜像分层

相关笔记：

> Docker：[https://github.com/fangkun119/java_proj_ref/tree/master/101_docker/note](https://github.com/fangkun119/java_proj_ref/tree/master/101_docker/note)

### CH05：Spring Cloud Configuration Server

笔记链接：[https://github.com/fangkun119/manning-smia/blob/master/note/ch05_spring_clound_configuration.md](https://github.com/fangkun119/manning-smia/blob/master/note/ch05_spring_clound_configuration.md)

内容概要：

> 1. 搭建Spring Cloud Config Server并与Spring Boot应用程序集成
> 2. 使用本地文件、git、vault ui作为配置存储
> 3. 实现配置文件多instance刷新
> 4. 使用对称加密存储敏感配置

### CH06：服务发现

笔记链接：[https://github.com/fangkun119/manning-smia/blob/master/note/ch06_service_discovery.md](https://github.com/fangkun119/manning-smia/blob/master/note/ch06_service_discovery.md)

内容概要：

> 1. 微服务服务发现 v.s 传统集中式服务发现
>
> 2. 客户端负载均衡
>
> 3. Spring Eureka Service搭建及配置
>
> 4. 向Eureka Service注册服务
>
> 5. 三种客户端通过Eureka Service和Spring Cloud Load Balancer调用服务的方法：
>
>     (1) 使用Service Discovery Client （没有负载均衡）
>
>     (2) 使用Load Balancer Awared RestTemplate
>
>     (3) 使用Netflix Feign Client

### CH07：弹性模式及Resilience4j

笔记链接：[https://github.com/fangkun119/manning-smia/blob/master/note/ch07_resilience4j_and_patterns.md](https://github.com/fangkun119/manning-smia/blob/master/note/ch07_resilience4j_and_patterns.md)

内容概要：

> (1) 断路器（Circuit Breaker）、隔离（Bulkhead）的工作原理
>
> (2) 使用Resilience4j（已处于维护模式的Hystrix的替代库）在Spring Cloud项目中实现`断路器`、`降级`、`隔离 `、`重试`、`Rate Limiter`等客户端弹性模式
>
> (3) 演示Resilience4j与ThreadLocal的兼容性

### CH08：服务路由及Spring Cloud Gateway

笔记链接：[https://github.com/fangkun119/manning-smia/blob/master/note/ch08_spring_cloud_gateway.md](https://github.com/fangkun119/manning-smia/blob/master/note/ch08_spring_cloud_gateway.md)

内容概要：

> (1) Gateway的作用，Spring Cloud Gateway以及与Eureka通信的配置
>
> (2) 自动路由配置和手动路由配置 
>
> (3) 使用内置的Predicates和Filter工厂
>
> (4) 编写自定义Pre-filter和Post-filter，在请求/响应/日志中添加correlation  id，从而将同一个客户端请求触发的所有REST调用关联起来

### CH09：微服务安全

笔记链接：[https://github.com/fangkun119/manning-smia/blob/master/note/ch09_security.md](https://github.com/fangkun119/manning-smia/blob/master/note/ch09_security.md)

内容概要：

> (1) 使用KeyCloak搭建开源身份验证授权服务并配置用户权限
>
> (2) 修改代码以使用KeyCloak的OAuth2认证保护单个服务
>
> (3) 让OAuth2 Access Token在服务调用之间传播、以保护所有服务
>
> (4) 解析并使用Access  Token中的信息

### CH10：事件驱动框架及Spring Cloud Stream

笔记链接：[https://github.com/fangkun119/manning-smia/blob/master/note/ch10_sprint_cloud_stream.md](https://github.com/fangkun119/manning-smia/blob/master/note/ch10_sprint_cloud_stream.md)

内容概要：

> (1) 消息队列与、事件驱动框架的优点和注意事项
>
> (2) Spring Cloud Stream对事件驱动框架的抽象
>
> (3) 使用Kafka以及Spring Cloud Stream实现消息的收发（使用默认channel、以及使用自定义channel）
>
> (4) 使用Redis作为数据缓存

### CH11：分布式tracing及Sleuth和Zipkin

笔记链接：https://github.com/fangkun119/manning-smia/blob/master/note/ch11_distributed_tracing.md

内容概要：

> (1) 使用Spring Cloud Sleuth为各个服务日志统一添加`trace id`、`span id`等；配置logstash-logback，将服务日志发送给LogStash
>
> (2) 使用ELK（ElasticSearch、LogStash、Kibana）以收集日志，配置并使用Kibana来根据`trace id`搜索属于同一transaction的日志
>
> (3) 将trace id添加到HTTP Response中以便日常oncall和debug
>
> (4) 使用Open Zipkin进行分布式调用耗时追踪，添加自定义span以追踪特定代码段的耗时情况

### CH12：微服务部署

笔记链接：[https://github.com/fangkun119/manning-smia/blob/master/note/ch12_deployment.md](https://github.com/fangkun119/manning-smia/blob/master/note/ch12_deployment.md)

内容概要：

> (1) 微服务构建和部署Pipeline的整体结构
>
> (2) 在AWS上部署数据库（PostgreSQL/Redis）、ELK（Elasticsearch/LogStash/Kibana）、微服务Service
>
> (3) 在Github和Jenkins上将构建和部署过程自动化

### 附录1：微服务架构最佳实践

### 附录2：OAuth2授权类型 

### 附录3：微服务监控

