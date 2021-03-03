#  Spring Integration Framework概述

> 文档：https://docs.spring.io/spring-integration/docs/current/reference/html/overview.html#spring-integration-introduction

## 1 背景

将Spring编程模型扩展到消息传递域，并基于Spring现有的企业集成支持来提供更高级别的抽象

提供了多种配置选项，包括：

> * 批注
> * 具有名称空间支持的XML
> * 具有通用“ bean”元素的XML
> * 直接使用底层API（基于定义明确的策略接口和非侵入性的委派适配器）

它遵循`pipe-and-filter`模型，`pipe`是传递消息的管道，而`filter`则是使用消息的组件



## 2 目标和原则

目标 ：

> - 实现复杂企业集成解决方案的简单模型
> - 促进异步，消息驱动的行为
> - 帮助开发者以直观，增量式的方式将其用在现有的Spring应用上

原则：

> * 组件松耦合，模块化，可测试
> * 业务逻辑和集成逻辑分离
> * 扩展点本质上应该是抽象的（但要在明确定义的边界之内），以促进重用和可移植性



## 3 主要组件

> Spring Integration的主要组件，在项目配置文件中也经常可以看到相关注解或xml配置

### 3.1 Message

Java对象的通用包装

> * Java对象序列化为Payload
> * 附加信息如ID、时间戳、相关ID、返回地址等作为消息头（header）作为包装附加到payload上

### 3.2 Channel

消息管道在Spring Integration层面的抽象，

> * 在`pipe-and-filter`架构中充当`pipe`的角色，生产者向channel发布消息，消费者订阅channel并消费消息，支持`点对点`通信以及`订阅发布`模型 
> * 它将消息通道（抽象层面）与底层的消息传递组件（例如kafka、rabbit mq等）分离
> * Channel接口有两种，使用方式不同：
>     * `PollableChannel`：轮询，需要配置轮询器，带有缓冲，消费者通过主动调用`receive(timeout)`来接收信息
>     * `SubscribableChannel`：发布订阅，无缓冲，只向消费者提供`subcribe`和`unsubscribe`两个方法
> * Channel的实现类有很多种、例如`PublishSubscribeChannel`、`QueueChannel`、`PriorityChannel`、`DirectChannel`等
>     * 他们有的只实现了上面的一个接口
>     * 有的两个接口都实现了可以以两种方式使用
>     * 具体参考：https://docs.spring.io/spring-integration/docs/current/reference/html/channel.html#channel-implementations

### 3.3 Endpoint

业务逻辑与Spring Integration

> Spring Intergration的目标之一是：不必实现生产者消费者，只实现业务逻辑，然后用声明性配置的方式，将业务逻辑`连接`到Spring Integration。而负责这个`连接`的组件就是EndPoint

框架提供了如下几种Endpoint

#### (1) Message Transformer

> 用来转换消息内容（例如xml转成json格式）和消息头

#### (2) Message Filter

> 用来缩小两个通道之间流动的消息的范围，通过检查消息内容或消息头，判断消息是否需要传递到输出通道

#### (3) Message Router

> 用来决定消息可以发送到哪个（或哪些）channel中，通常用作`service activator`或其他`end point`的动态替代方案

#### (4) Splitter

> 从input channel的消息拆分为多条消息，每条消息发送到对应的output channel

#### (5) Aggregator

> 接收多条消息，并将它们组合成一条消息（与Splitter相反），通常它们需要维护更多的状态（例如确定何时收到完整的消息组，超时时的处理方式等），这些可通过`CorrelationStrategy`，`ReleaseStrategy`等配置）

#### (6) Service Activator

> 将业务逻辑连接到消息系统的通用端点上 ，必须为它指定`input channel`，可选指定`output channel`，也可以在每条输出的消息头中指定该消息的返回地址
>
> 关系示意图 如下（时钟及实线箭头对应于轮询模型，虚线箭头对应于订阅模型）：
>
> <div align="left"><img src="https://docs.spring.io/spring-integration/docs/current/reference/html/images/handler-endpoint.jpg" width="800" /></div>

#### (7) Channel Adapter

> 将channel连接到其他系统（例如kafka、rabbit MQ等），分为inbound channel adaptor和outbound channel adaptor，图示如下（时钟及实线箭头对应于轮询模型，虚线箭头对应于订阅模型）
>
> * inbound channel adaptor
>
>     <div align="left"><img src="https://docs.spring.io/spring-integration/docs/current/reference/html/images/source-endpoint.jpg" width="800" /></div>
>
> * outbound channel adaptor
>
>     <div align="left"><img src="https://docs.spring.io/spring-integration/docs/current/reference/html/images/target-endpoint.jpg" width="800" /></div>







