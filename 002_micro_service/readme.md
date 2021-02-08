[TOC]

# Microservice and Spring Cloud笔记

## Spring Microservices in Action, 第二版

在线阅读：[https://livebook.manning.com/book/spring-microservices-in-action-second-edition/](https://livebook.manning.com/book/spring-microservices-in-action-second-edition/)

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

### CH07：容错模式及Resilience4j

笔记链接：

内容概要：

> 

### CH08：服务路由及Spring Cloud Gateway

笔记链接：

内容概要：

> 

### CH09：微服务安全

笔记链接：

内容概要：

> 

### CH10：事件驱动框架及Spring Cloud Stream

笔记链接：

内容概要：

> 

### CH11：分布式tracing及Sleuth和Zipkin

笔记链接：

内容概要：

> 

### CH12：微服务部署

笔记链接：

内容概要：

> 

### 附录1：微服务架构最佳实践

### 附录2：OAuth2授权类型 

### 附录3：微服务监控

