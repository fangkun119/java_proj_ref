[TOC]

## 01 老式单体架构对比微服务架构

|            | 单体架构                               | 微服务                                                       |
| ---------- | -------------------------------------- | ------------------------------------------------------------ |
| 模块关系   | 耦合                                   | 各功能解耦                                                   |
| 系统复杂度 | 单模块升级导致整个服务升级             | 单个服务升级不影响其他服务                                   |
| 扩展性     | 难以只为单个模块扩容                   | 可以为某个服务进行单独扩容或缩容                             |
| 协作       | 各部门共用code base                    | 各部门协作明晰                                               |
| 故障隔离   | 单模块故障导致整个服务不可用           | 单个服务故障可以隔离（bulkhead），不影响其他服务。除此之外还有断路器、降级、流量控制、负载均衡、自动重试等各种方式来增加服务的resiliency |
| 技术语言   | 单一技术和语言                         | 可以为不同的服务选用合适的技术架构或语言                     |
| 数据库     | 共用数据库，数据库故障导致整个服务崩溃 | 数据库独立，互不干扰                                         |

## 02 微服务组件

### (1) 核心服务

> 服务粒度：Domain Driven Design
>
> 通信协议：同步、异步
>
> 接口设计
>
> 配置统一管理：Spring Cloud Configuration Server
>
> 使用事件让服务解耦：	

### (2) 路由模式

> 服务发现：Eureka（另外还有etcd、Consul、zookeeper)
>
> 服务路由：Spring Cloud API Gateway

### (3) 客户端Resiliency模式

> 客户端负载均衡：使用Spring Cloud Load Balancer 
>
> 断路器Circuit Breaker：使用Resilience4J，对频繁请求的客户端或异常的back-service，启用fast fail
>
> 降级模式Fallback：使用Resilience4J，服务调用失败时，让客户端通过备选方式完成
>
> 隔离Bulkhead：使用Resilience4J，内部服务之间的故障隔离，防止调用某个服务独占线程池
>
> 流量控制RateLimiter：使用Resilience4J，限制请求量
>
> 自动重试Retry：使用Resilience4J

### (4) API Gateway模式

> 客户端身份认证，权限检查，秘钥传递 ：使用Spring Cloud Gateway，Spring Cloud Security

### (5) 微服务日志收集

>  日志收集、聚合：使用Logstash、Kibana、Elasticsearch
>
> 微服务调用追踪：使用Spring Cloud Sleuth（生成ID）

### (6) 性能指标度量

> 性能度量指标收集
>
> 性能度量指标存储查询	

### (7) 开发和部署

> 创建部署Pipeline	
>
> 自动构建镜像并打包
>
> 镜像一旦创建就不可变
>
> 定期用镜像重部署	

### (8) 消息中间件集成

> 使用Spring Cloud Stream

## 03 服务熔断

> * 用途：应对服务雪崩的链路保护
> * 触发：某个backing service不可用或者想听超时
> * 应对：熔断对该backing service的调用、直接返回“Error Response”而不是随着该backing service一起超时
> * 恢复：检测到backing service正常后解除熔断、恢复调用链路
> * 组件：Resilience4J（替换Netflix Hystrix），Alibaba Sentinel