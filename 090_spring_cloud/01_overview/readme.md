# Spring Cloud内容概要 

## 1. 知识点

### (1) 前置知识
* Spring Boot
* Spring Data JPA，JDBC，MyBatis
* Spring Data ES & Solr  & Redis & Rest
* ThymeLeaf

### (2) 快速上手
* Eureka 
* Ribbon
* Hystrix
* OpenFeign
* Zuul
* Gateway
* Config
* Sleuth
* Cloud  Load Balancer

### (3) 完整内容 
* Spring Boot Configuration Processor
* 响应式Web
	* Spring Reactive Web
	* DevTools
	* Lombok
* Amazon Web Services
* Alibaba Nacos
* Alibaba Sentinel
* Consul Discovery
* 权限与单点登录设计
* 分布式事务
* 消息
	* Spring For RabbitMq
	* Spring For Kafaka
	* Spring For Active MQ
	* RocketMQ整合
* 分布式锁
* 企业级消息总线
	*  Camel
	*  Bus
* Spring Batch
* Validation
* Mail Sender
* Quartz
* XXL-Job
* Docker容器化部署
* Web
	* Rest Repositories
	* 使用 Spring HATEOAS开发REST服务
	* Jersey
* Spring Session
* Spring WebService
* Cloud Foundry
* Kubernetes
* Zipkin
* OAuth2
* Security
* Stream
* 运维监控
	* Actuator
	* Admin 

### (4) 微服务架构演变 、分布式系统设计原理

*  微服务系统设计与老旧项目微服务改造
*  高可用高并发系统架构设计
*  电商架构
*  App后端架构
*  业务中台架构

### (5) 其他

* ELK
* Service Mesh
* Jenkins + SonarQube

## 2. 各组件功能

> `Spring Cloud`的组件提供方主要包括：`Netflix`，`Alibaba`，`Apache`

### (1) 请求接入端

* `Netflix Eureka`	：微服务注册中心 
* `Netflix feign`	：微服务调用
* `Ribbon`	        	：微服务提供端负载均衡
* `Netflix Zuul`		：
* `Spring Security OAuth 2.0` + `JWT`：单点认证及Token生成
* `Spring Session`	：会话共享（如果使用OAuth  + JWT就不需要Session共享）

### (2) 微服务调用端

* `Alibaba Senta`（最近流行）/`Alibaba  Gts`（比较旧）：分布式事务框架
* `Spring Cloud Sleuth`：微服务调用链路追踪
* `Zookeeper`/`Curator`/`RedLock`：分布式锁
* `Netflix Hystrix`：熔断和降级 
* `Netflix Ribbon`：微服务提供端负载均衡
* `Actuator`：微服务状态上报（报给 Netflix Hystrix和Spring Cloud Admin）
* `Spring Cloud Admin`：整体状态监控

### (3) 消息中间件

**企业化信息集成**

* `Spring Message`：消息包装、拼比框架（Kafaka、Rabbit MQ等）之间 的差异
* `Spring Integration`：对`Spring Message`进行包装、集成多种消息队列，提供分发、路由、消息过滤、消息聚合、Topic拆分等功能
* `Spring Stream`：基于`Spring  Integration`，与`Sping Boot`整合，作为`Spring Cloud Bus`的基础组件
* `Spring Cloud Bus` / `Apache Camel`：配置文件分发、所有消息中间件集成在一起，遵循AMQP

**消息队列直接连接**

* `Spring For RabbitMQ`
* `Spring For Kafaka`
* `Spring For ActiveMQ`
* `RocketMQ整合`

> 使用`Spring Cloud`的主要是极大的项目，普通的分布式项目`Dubbo`就够了

