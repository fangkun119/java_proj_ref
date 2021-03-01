# Kafka笔记

## 1. Kafka介绍

笔记：[01_conception/readme.md](01_conception/readme.md)

内容

> Kafka在系统设计中的三个作用；工作模式；零拷贝IO的特性

## 2. 单节点Kafka环境搭建

笔记：

> `Virtual Box + CentOS`：[02_single_node_setup/readme.md](02_single_node_setup/readme.md)
>
> `Mac`：[02_single_node_setup/single_node_kafka_on_mac.md](02_single_node_setup/single_node_kafka_on_mac.md)

内容

> Kafka环境搭建过程记录及问题解决

## 3. 多节点Kafka环境搭建

笔记：[03_cluster_setup/readme.md](03_cluster_setup/readme.md)

内容

> 在`Virtual Box + CentOS`环境上，搭建多节点Kafka环境

## 4. 命令行TOPIC管理、消息订阅和发布

笔记：[04_topic_mgmt/readme.md](04_topic_mgmt/readme.md)

内容

> 使用Kafka提供的命令行工具，进行Topic管理，消息订阅和发布

## 5. 基础Java API

笔记：[05_java_api_basic/readme.md](05_java_api_basic/readme.md)

内容：基础Java API代码及Demo【[代码](https://github.com/fangkun119/java_proj_ref/tree/master/300_kafka/demos)】

> (1) topic管理
>
> (2) 消息发布和消费
>
> (3) 手动指定消费分区
>
> (4) 生产者分区策略
>
> (5) 序列化
>
> (6) 拦截器

## 6. 高级Java API

笔记：[06_java_api_advanced/readme.md](06_java_api_advanced/readme.md)

内容：高级Java API代码及Demo【[代码](https://github.com/fangkun119/java_proj_ref/tree/master/300_kafka/demos)】

> (1) Consumer Group漂移量初始值设置
>
> (2) 由消费者管理偏移量
>
> (3) 生产者Acks级别和Retires配置
>
> (4) 幂等写
>
> (5) 生产者Only事务
>
> (6) 消费者&生产者事务

## 7. 高级主题

笔记：[07_advanced_topics/readme.md](07_advanced_topics/readme.md)

内容：

> (1) 老版本`High Water Mark`机制的问题、以及0.11版本之后`Leader Epoach`机制的补救
>
> (2) 异步发送、同步发送、异步回调
>
> (3) Spring-Kafka 官网Demo【[代码](https://github.com/fangkun119/java_proj_ref/tree/master/300_kafka/spring_kafka_samples)】
>
> * 生产者消费者及Error Recovery 
> * Multi-Method Listener 
> * 事务
>
> (4) 在Spring Cloud Stream中使用Kafka：参考另一篇笔记[Spring Cloud Stream事件驱动架构](https://github.com/fangkun119/manning-smia/blob/master/note/ch10_sprint_cloud_stream.md)

