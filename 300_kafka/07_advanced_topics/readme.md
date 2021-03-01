<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Kafka其他主题](#kafka%E5%85%B6%E4%BB%96%E4%B8%BB%E9%A2%98)
  - [1. High Water Mark机制与Leader Epoach机制](#1-high-water-mark%E6%9C%BA%E5%88%B6%E4%B8%8Eleader-epoach%E6%9C%BA%E5%88%B6)
    - [(1) High Water Mark机制](#1-high-water-mark%E6%9C%BA%E5%88%B6)
    - [(2) Leader Epoch机制](#2-leader-epoch%E6%9C%BA%E5%88%B6)
  - [2 同步发送、异步发送和异步回调](#2-%E5%90%8C%E6%AD%A5%E5%8F%91%E9%80%81%E5%BC%82%E6%AD%A5%E5%8F%91%E9%80%81%E5%92%8C%E5%BC%82%E6%AD%A5%E5%9B%9E%E8%B0%83)
    - [2.1 异步发送](#21-%E5%BC%82%E6%AD%A5%E5%8F%91%E9%80%81)
    - [2.2 同步发送](#22-%E5%90%8C%E6%AD%A5%E5%8F%91%E9%80%81)
    - [2.3 异步回调](#23-%E5%BC%82%E6%AD%A5%E5%9B%9E%E8%B0%83)
    - [2.4 参考](#24-%E5%8F%82%E8%80%83)
  - [3 spring-kafka](#3-spring-kafka)
    - [3.1 Demo 1:  基本功能以及错误恢复(Error Recovery)](#31-demo-1--%E5%9F%BA%E6%9C%AC%E5%8A%9F%E8%83%BD%E4%BB%A5%E5%8F%8A%E9%94%99%E8%AF%AF%E6%81%A2%E5%A4%8Derror-recovery)
    - [3.2 Demo 2: Multi-Method Lintener](#32-demo-2-multi-method-lintener)
    - [3.3 Demo 3: 事务](#33-demo-3-%E4%BA%8B%E5%8A%A1)
    - [3.4 参考](#34-%E5%8F%82%E8%80%83)
    - [3.5 相关问题](#35-%E7%9B%B8%E5%85%B3%E9%97%AE%E9%A2%98)
  - [4 在Spring Cloud Stream中使用Kafka](#4-%E5%9C%A8spring-cloud-stream%E4%B8%AD%E4%BD%BF%E7%94%A8kafka)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Kafka其他主题

## 1. High Water Mark机制与Leader Epoach机制

### (1) High Water Mark机制

0.11版本之前的Kafka使用高水位机制来维护Leader和Follower之间的数据同步状态

> * `Topic`被分为多个`分区`，`分区`按照`Segments`存储文件块，这样可以根据偏移量快速查找分区块，Kafka可以保证分区里的事件是有序的，`Leader`负责对分区的读写、`Follower`负责同步分区的数据
> * 高水位机制被用于`0.11`版本之前Kafka的`Leader`和`Followers`建的数据同步（低于`HW`的`Record`表示已经同步给了所有`Followers`，而高于`HW`则不保证）

相关概念

> * `Log End Offset` （简称`LEO`）：标记分区中最后一条消息的下一个位置（下一条消息的写入位置）、每个副本都有自己的`LEO`
>
> * `High Water Marker`（简称`HW`）：高水位线，所有`HW`之前的数据都已经备份、当新的数据在所有节点都备份成功、Leader会更新高水位线
>
> * `In-Sync-Replicas`（简称`ISR`）：`Leader`维护一份处于同步中的`Follower`（副本节点、Broker）集合，如果一个`Follower`在`replica.lag.time.max.ms`（Kafka 0.9.0及之后的版本）时间内内：
>
>   * 该节点没有发送`fetch`请求向Leader请求数据
>   * 或者该节点发送了`fetch`请求、但是没有赶上Leader的数据
>
>   该`Follower`就会被从集合中剔除。（备注：在0.9.0版本之前的`Kafka`使用`replica.lag.max.messages`、该配置会导致其他Broker节点频繁加入和退出ISR、因此被废除）

高水位机制（0.11版本之前）的问题：因为下面两个问题，建议使用0.11之后的版本

> * Follower重启引发HW截断，同时Leader宕机使Follower（截断尚未恢复）成为Leader时，引发数据丢失（[High Watermark Truncation followed by Immediate Leader Election](https://cwiki.apache.org/confluence/display/KAFKA/KIP-101+-+Alter+Replication+Protocol+to+use+Leader+Epoch+rather+than+High+Watermark+for+Truncation#KIP101AlterReplicationProtocoltouseLeaderEpochratherthanHighWatermarkforTruncation-Scenario1:HighWatermarkTruncationfollowedbyImmediateLeaderElection)）
>
>   <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_dataloss_s1.jpg" width="800" /></div>
>
> * Follower与Leader同时重启，先启动的Follower（截断尚未恢复）接收新数据时，引发数据不一致的问题（[Replica Divergence on Restart after Multiple Hard Failures](https://cwiki.apache.org/confluence/display/KAFKA/KIP-101+-+Alter+Replication+Protocol+to+use+Leader+Epoch+rather+than+High+Watermark+for+Truncation#KIP101AlterReplicationProtocoltouseLeaderEpochratherthanHighWatermarkforTruncation-Scenario2:ReplicaDivergenceonRestartafterMultipleHardFailures)）
>
>   <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_replica_divergence_s1.jpg" width="800" /></div>

### (2) Leader Epoch机制

Kafka 0.11版本引入[`Leader Epoch`机制](https://cwiki.apache.org/confluence/display/KAFKA/KIP-101+-+Alter+Replication+Protocol+to+use+Leader+Epoch+rather+than+High+Watermark+for+Truncation#KIP101AlterReplicationProtocoltouseLeaderEpochratherthanHighWatermarkforTruncation-Solution)来解决`High Water Mark机制`的数据就是和不一致问题

> * 之前的`High Water Mark机制`仍然在使用、但只用于衡量同步状态，而不再用作数据截断的依据
>
> * 数据截断交给`Leader Epoch`来判断

过程如下图

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_epoch.jpg" width="1024" /></div>
>
> 
>
> * `Leader Epoch`是一个维护在Zookeeper上的32位数字，表示是第几次Leader选举得出的Leader版本，由`Controller`来管理，存储在`Zookeeper`的分区状态信息中。发生`Leader`选举时，`Leader Epoch`会作为`LeaderAndIsrRequest`的一部分传给每一个新的Leader
>
> * 生产者发送消息给`Leader`时，Leader会给这条消息标记上`Leader Epoch`信息
>
> * 不论`Leader`还是`Follower`都会维护一份`Leader Epoch Sequence`序列文件（格式为List<leader_epoch__id, start_offset>），标记了历史上各个`Leader Epoch`所对应的分区偏移量
>
>   * `Follower`变成`Leader`时，将新的`Leader Epoch`和它的`LEO`添加到`Leader Epoch Sequence`文件，并且该`Leader`之后产生的每个新消息都带有新的`Leader Epoch`标记
>
>   * `Leader`变成`Follower`时，向新选举的`Leader`发送`Leader Epoch Request`，请求包含当前（已变成follower）节点所存最新的`<epoch_id, start_offset>`，而新`Leader`则返回`Leader`自身`最新epoch_id`的`<epoch_id, start_offset或LEO>`
>
>     * 返回哪个值取决于request中的epoch_id是否与新leader的epoch_id相等
>
>       * 对于来自旧leader或的“萌新”节点请求，epoch_id会比新leader的小，因此返回`<epoch_id, start_offset>`来帮助它把前一个epoch的数据先同步完
>       * 对于来自其他follower的请求，epoch_id相等，因此返回`<epoch_id, LEO>`仅仅帮助它追赶当前epoch的数据
>
>     * Follower收到新Leader的Response后，分两种情形
>
>       * `Follower`的`offset`比`Leader`小（左图对应`epoch_id`比新leader的小的情况；右图对应`epoch_id`相等的情况）：Follower根据Leader的offset来追赶数据
>
>         <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_leaderepoch_sync_s1.jpg" width="900" /></div>
>
>       * `Follower`的`start_offset`比`Leader`的返回的`offset`大（左图对应`epoch_id`比新leader的小的情况；右图对应`epoch_id`相等的情况）：Follower根据Leader的数据重置自己的`Leader Epoch文件`并进行日志截断
>
>         <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_leaderepoch_sync_s2.jpg" width="900" /></div>

用`Leader Epoch机制`解决`高水位机制`的两个问题

> * 解决“Follower重启引发HW截断，同时Leader宕机使Follower（截断尚未恢复）成为Leader时，引发数据丢失（[High Watermark Truncation followed by Immediate Leader Election](https://cwiki.apache.org/confluence/display/KAFKA/KIP-101+-+Alter+Replication+Protocol+to+use+Leader+Epoch+rather+than+High+Watermark+for+Truncation#KIP101AlterReplicationProtocoltouseLeaderEpochratherthanHighWatermarkforTruncation-Scenario1:HighWatermarkTruncationfollowedbyImmediateLeaderElection)）”的问题
>
>   <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_leaderepoch_solve_dataloss.jpg" width="900" /></div>
>
>   原问题：A重启由于HW还未更新、将m2截断丢弃并连接Leader B同步数据；然而随后B宕机A变成Leader导致消息m2丢失
>
>   引入Epoch后：A重启后并不会根据HW截断m2，而是向B发送Leader Epoch Request，B返回offset 2，B随后宕机发生重新选举使A成为Leader，A生成新的Leader Epoch <lg=2; offset=2>接收数据。因为在A成为leader之前，A的offset不可能比B高，根据上一小节的内容，A不会发生截断，因此也就没机会丢失消息m
>
> * 解决“Follower与Leader同时重启，先启动的Follower（截断尚未恢复）接收新数据时，引发数据不一致的问题（[Replica Divergence on Restart after Multiple Hard Failures](https://cwiki.apache.org/confluence/display/KAFKA/KIP-101+-+Alter+Replication+Protocol+to+use+Leader+Epoch+rather+than+High+Watermark+for+Truncation#KIP101AlterReplicationProtocoltouseLeaderEpochratherthanHighWatermarkforTruncation-Scenario2:ReplicaDivergenceonRestartafterMultipleHardFailures)）”的问题
>
>   <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_leaderepoch_solve_inconsist.jpg" width="900" /></div>
>
>   原问题：A、B同时重启，Follower B先启动成为Leader接收数据m3，随后A也启动完成但相同offset位置的数据是m2，引发数据不一致
>
>   引入Epoch后：A启动完成后先向B发送Leader Epoch  Request `<lg=0, offset=0>`，B返回了`<lg=1,offset=1>`，根据上一小节的内容 ，B返回的offset=1比 A的`start_offset=0`高 ，因此A知道发生了数据不一致，截断消息 m2，从B同步消息m3

## 2 同步发送、异步发送和异步回调

### 2.1 异步发送

> **在未开启事务时**，调用`producer.send`只代表消息已经提交给了生产者底层的API，此时消息有可能只是缓存在Producer又或者已经交给`Leader Broker`但还没有返回ACK，但不论哪种生产者都认为消息已经发送完毕
>
> ~~~java
> byte[] key = "key".getBytes();
> byte[] value = "value".getBytes();
> ProducerRecord<byte[],byte[]> record = new ProducerRecord<byte[],byte[]>("my-topic", key, value)
> producer.send(record)
> ~~~
>
> **如果已经开启了事务**，可以忽略下面的`同步发送`和`异步回调`，直接调用`producer.send`即可。
>
> 如果事务中任何一条消息发送失败，都会导致`producer.commitTransaction() `失败抛出异常。此时在catch块中可调用`producer.abortTransaction()`并重置队列状态，也可以知道该事务下所有的消息都没能提交给Kafka。

### 2.2 同步发送

> `producer.send`会返的类型是`Future<RecordMetadata>`，调用这个返回对象的`.get()`方法可以让生产者线程阻塞直到收到`Leader Broker`返回的ACK（可以向`.get()`方法传入阻塞超时时间）
>
> ~~~java
> byte[] key = "key".getBytes();
> byte[] value = "value".getBytes();
> ProducerRecord<byte[],byte[]> record = new ProducerRecord<byte[],byte[]>("my-topic", key, value)
> producer.send(record).get() // 阻塞等待Leader Broker返回的ACK
> ~~~

### 2.3 异步回调

> 在调用`send`方法时可以设置回调函数，该函数在收到ACK或者发生异常时被调用
>
> ```java
> producer.send(
>      record,
>      new Callback() {
>          public void onCompletion(RecordMetadata metadata, Exception e) {
>              if(e != null) {
>                  e.printStackTrace();
>              } else {
>                  System.out.println("The offset of the record we just sent is: " + metadata.offset());
>              }
>          }
>      }
> );
> ```
>
> **`Producer`何时认为自己收到了`ACK`**：取决于配置项`acks`：
>
> * 为`0`消息进入生产者`socket`队列即视为已经ACK
> * 为`1`(默认配置)消息被`Leader Broker`写入本地日志即可以返回ACK
> * 为`all`要等待`Leader Broker`把消息同步给所有`Follower Broker`才会返回ACK
>
> 另外还有一个配置是`幂等写`，`enable.idempotence`，为true时消费者会开启过滤，去除哪些因为生产者重试而重复发送的数据

### 2.4 参考

> * [300_kafka/06_java_api_advanced/readme.md](https://github.com/fangkun119/java_proj_ref/tree/master/300_kafka/06_java_api_advanced)
> * org.apache.kafka.clients.producer.KafkaProducer源代码注释

## 3 spring-kafka

> * 官网：[https://spring.io/projects/spring-kafka](https://spring.io/projects/spring-kafka)
> * 文档：https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#sending-messages
> * 调试环境： [../02_single_node_setup/single_node_kafka_on_mac.md](../02_single_node_setup/single_node_kafka_on_mac.md)

### 3.1 Demo 1:  基本功能以及错误恢复(Error Recovery)

功能点：代码在[官方Demo: spring-kafka/samples/sample-01](https://github.com/spring-projects/spring-kafka/tree/master/samples/sample-01)的基础础上进行修改，包含如下功能

> * 在SpringBoot项目中引入spring-kafka，实现生产者消费者功能
> * 如何在[@Configuration类](../spring_kafka_samples/sample-01/src/main/java/com/javaprojref/spring_kafka/pnc_demo/config/KafkaConfig.java)中对生产者消费者进行配置
> * 消费者接收消息处理出错时，进行2次重试，如果仍然失败则将消息转发到专门存放失败消息的topic中
>
> 各功能均注释了对应的文档链接

Demo项目：

> * [../spring_kafka_samples/sample-01/](../spring_kafka_samples/sample-01/)

主要代码：

> * [spring_kafka_sample/pnc_demo/controller/Controller.java](../spring_kafka_samples/sample-01/src/main/java/com/javaprojref/spring_kafka/pnc_demo/controller/Controller.java)
> * [spring_kafka_sample/pnc_demo/config/KafkaConfig.java](../spring_kafka_samples/sample-01/src/main/java/com/javaprojref/spring_kafka/pnc_demo/config/KafkaConfig.java)

运行：

(1) 启动程序：SpringBoot启动、打印Spring和Kafka日志后，进入监听状态：

(2) 在另一个终端窗口，发送一条正常的POST请求

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ curl -X POST http://localhost:8080/send/topic01/message01
> ~~~
>
> SpringBoot打印日志，表示已经收到该消息
>
> ~~~bash
> 2020-12-21 15:59:26.010  INFO 33302 --- [oListener-0-C-1] com.example.Application                  : recieve message from topic01: Foo2 [foo=message01]
> ~~~

(3) 接下来发送一条会触发异常的请求

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/tmp/
> $ curl -X POST http://localhost:8080/send/topic01/fail01
> ~~~
>
> ```java
> @KafkaListener(id = "fooListener", topics = "topic01")
> public void topic01Listener(Foo2 foo) {
>    	logger.info("recieve message from topic01: " + foo);
>    	if (foo.getFoo().startsWith("fail")) {
>    		throw new RuntimeException("failed"); //触发异常，交给errorHandler来处理
>    	}
>    	this.exec.execute(() -> System.out.println("Hit Enter to terminate..."));
> }
> ```
>
> `spring-kafka`的Consumer会尝试处理这条来自topic01的消息三次（1次尝试、2次重试），第4次时放弃处理将其转发到topic01.DLT
>
> ~~~bash
> Hit Enter to terminate...
> 2020-12-21 16:01:44.679  INFO 33302 --- [oListener-0-C-1] com.example.Application                  : recieve message from topic01: Foo2 [foo=fail01]
> 2020-12-21 16:01:45.684  INFO 33302 --- [oListener-0-C-1] o.a.k.clients.consumer.KafkaConsumer     : [Consumer clientId=consumer-fooListener-1, groupId=fooListener] Seeking to offset 1 for partition topic01-0
> 2020-12-21 16:01:45.710 ERROR 33302 --- [oListener-0-C-1] essageListenerContainer$ListenerConsumer : Error handler threw an exception
> ...
> ...
> ...
> 2020-12-21 16:01:45.712  INFO 33302 --- [oListener-0-C-1] com.example.Application                  : recieve message from topic01: Foo2 [foo=fail01]
> 2020-12-21 16:01:46.713  INFO 33302 --- [oListener-0-C-1] o.a.k.clients.consumer.KafkaConsumer     : [Consumer clientId=consumer-fooListener-1, groupId=fooListener] Seeking to offset 1 for partition topic01-0
> 2020-12-21 16:01:46.714 ERROR 33302 --- [oListener-0-C-1] essageListenerContainer$ListenerConsumer : Error handler threw an exception
> ...
> ...
> ...
> 2020-12-21 16:01:45.712  INFO 33302 --- [oListener-0-C-1] com.example.Application                  : recieve message from topic01: Foo2 [foo=fail01]
> 2020-12-21 16:01:46.713  INFO 33302 --- [oListener-0-C-1] o.a.k.clients.consumer.KafkaConsumer     : [Consumer clientId=consumer-fooListener-1, groupId=fooListener] Seeking to offset 1 for partition topic01-0
> 2020-12-21 16:01:46.714 ERROR 33302 --- [oListener-0-C-1] essageListenerContainer$ListenerConsumer : Error handler threw an exception
> ...
> ...
> ...
> 2020-12-21 16:01:46.715  INFO 33302 --- [oListener-0-C-1] com.example.Application                  : recieve message from topic01: Foo2 [foo=fail01]
> 2020-12-21 16:01:46.725  INFO 33302 --- [     id02-0-C-1] com.example.Application                  : receive message from topic01.DLT: {"foo":"fail01"}
> Hit Enter to terminate...
> ~~~

(4) 关闭Spring Boot

### 3.2 Demo 2: Multi-Method Lintener

功能点：

> * 生产者将不同类型的数据序列化成json时，标记不同的TYPE_ID
>
> * 消费者在接收数据时，根据TYPE_ID将json反序列化为不同类型的对象
>
> * 设置MultiMethodListener，为不同的对象类型指派不同的处理方法
> * 在[application.yml](../spring_kafka_samples/sample-02/src/main/resources/application.yml)中配置Kafka的Producer和Consumer

Demo项目：

> * [../spring_kafka_samples/sample-02/](../spring_kafka_samples/sample-02/)

主要代码：

>* [spring_kafka/multi_method_demo/controller/Controller.java]( ../spring_kafka_samples/sample-02/src/main/java/com/javaprojref/spring_kafka/multi_method_demo/controller/Controller.java)
>
>* [spring_kafka/multi_method_demo/config/KafkaConfig.java](../spring_kafka_samples/sample-02/src/main/java/com/javaprojref/spring_kafka/multi_method_demo/config/KafkaConfig.java)
>* [spring_kafka/multi_method_demo/config/KafkaListenerConfig.java](../spring_kafka_samples/sample-02/src/main/java/com/javaprojref/spring_kafka/multi_method_demo/config/KafkaListenerConfig.java)
>* [application.yml](../spring_kafka_samples/sample-02/src/main/resources/application.yml)

运行

(1) 启动程序：SpringBoot启动、打印Spring和Kafka日志后，进入监听状态

(2) 在另一个终端窗口，发送3个POST请求，会触发生产者发送三条消息

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ curl -X POST http://localhost:8080/send/foo/bar
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ curl -X POST http://localhost:8080/send/bar/baz
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ curl -X POST http://localhost:8080/send/unknown/xxx
> ~~~

(3) 在Spring Boot的日志中可以看到，三条请求分别落到`MultiMethodsListener`的三个不同`Handler`中

> ~~~bash
> 2020-12-22 10:23:46.313  INFO 38904 --- [ad | producer-1] org.apache.kafka.clients.Metadata        : [Producer clientId=producer-1] Cluster ID: drZAbZHiRuK5iwtychQ2Qw
> Received: Foo2 [foo=bar]
> Received: Bar2 [bar=baz]
> Received unknown: xxx
> ~~~
>
> 对应的代码
>
> ```jade
> @Component
> @KafkaListener(id = "multiGroup", topics = { "foos", "bars" })
> public class MultiMethodsListener {
>    	@KafkaHandler
>    	public void foo(Foo2 foo) {
>    		System.out.println("Received: " + foo);
>    	}
> 
>    	@KafkaHandler
>    	public void bar(Bar2 bar) {
>    		System.out.println("Received: " + bar);
>    	}
> 
>    	@KafkaHandler(isDefault = true)
>    	public void unknown(Object object) {
>    		System.out.println("Received unknown: " + object);
>    	}
> }
> ```

(4) 关闭Spring  Boot

### 3.3 Demo 3: 事务

功能：

> * 在application.yml中配置producer并开启事务特性，同时配置consumer
> * 以事务的方式发送消息
> * 使用`BatchMessagingMessageConverter`来对上游发来的一组消息进行反序列化
> * 将消费者的处理逻辑拆分到service层

主要代码：

> * [application.yml](../spring_kafka_samples/sample-03/src/main/resources/application.yml)
> * [transaction_demo/config/KafkaConfig.java](../spring_kafka_samples/sample-03/src/main/java/com/javaprojref/spring_kafka/transaction_demo/config/KafkaConfig.java)
> * [transaction_demo/controller/Controller.java](../spring_kafka_samples/sample-03/src/main/java/com/javaprojref/spring_kafka/transaction_demo/controller/Controller.java)
> * [transaction_demo/service/kafka_listener/KafkaListenerHandler.java](../spring_kafka_samples/sample-03/src/main/java/com/javaprojref/spring_kafka/transaction_demo/service/kafka_listener/KafkaListenerHandler.java)

运行：

(1) 启动程序：SpringBoot启动、打印Spring和Kafka日志后，进入监听状态

> ~~~bash
> ......
> 2020-12-22 10:38:59.555  INFO 39003 --- [2Listener-0-C-1] o.s.k.l.KafkaMessageListenerContainer    : topic02Listener: partitions assigned: [topic02-0]
> ~~~

(2) 在另一个终端窗口，发送POST请求，该请求携带5条消息，将触发生产者在1个事务中将其全部发送到topic02

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ curl -X POST http://localhost:8080/send/foos/a,b,c,d,e
> ~~~
>
> ```java
> this.template.executeInTransaction(kafkaTemplate -> {
>    	StringUtils.commaDelimitedListToSet(commaDelimitedMsgList).stream()
>    		.map(s -> new Foo1(s))
>    		.forEach(foo -> kafkaTemplate.send("topic02", foo));
>    	return null;
> });
> ```

(3) 可以看到

> 消费者在一个事务中，收到来自topic02的5条消息
>
> ~~~bash
> 2020-12-22 10:40:11.742  INFO 39003 --- [tener.topic02.0] o.a.k.c.p.internals.TransactionManager   : [Producer clientId=producer-tx.topic02Listener.topic02.0, transactionalId=tx.topic02Listener.topic02.0] ProducerId set to 4001 with epoch 5
> 2020-12-22 10:40:11.742  INFO 39003 --- [2Listener-0-C-1] c.j.s.transaction_demo.Application       : Received: [Foo2 [foo=a], Foo2 [foo=b], Foo2 [foo=c], Foo2 [foo=d], Foo2 [foo=e]]
> ~~~
>
> ```java
> // 在一个完整事务中发送一组消息
> this.template.executeInTransaction(kafkaTemplate -> {
>    	StringUtils.commaDelimitedListToSet(commaDelimitedMsgList).stream()
>    		.map(s -> new Foo1(s))
>    		.forEach(foo -> kafkaTemplate.send("topic02", foo));
>    	return null;
> });
> ```
>
> 随后生产者将这5条消息以非事务的方式转发到topic03
>
> ~~~bash
> 2020-12-22 10:40:11.745  INFO 39003 --- [2Listener-0-C-1] c.j.s.transaction_demo.Application       : essages forwarded to topic03, hit Enter to continue
> ~~~
>
> ```java
> public void handleTopic02(List<Foo2> foos) throws IOException {
>    	LOGGER.info("Received: " + foos);
>    	foos.forEach(f -> kafkaTemplate.send("topic03", f.getFoo().toUpperCase()));
>    	LOGGER.info("Messages forwarded to topic03, hit Enter to continue");
>    	System.in.read();
> }
> ```
>
> 输入回车，消费者在topic03收到了转发的5条消息
>
> ~~~bash
> o.a.k.c.p.internals.TransactionManager   : [Producer clientId=producer-tx.topic03listener.topic03.0, transactionalId=tx.topic03listener.topic03.0] ProducerId set to 4002 with epoch 1
> 2020-12-22 10:40:20.644  INFO 39003 --- [3listener-0-C-1] c.j.s.transaction_demo.Application       : Received: [A, B, C, D, E]
> ~~~
>
> 随后 根据主线程的计算逻辑、程序退出
>
> ~~~bash
> 2020-12-22 10:40:20.647  INFO 39003 --- [tener.topic03.0] o.s.s.concurrent.ThreadPoolTaskExecutor  : Shutting down ExecutorService 'applicationTaskExecutor'
> 2020-12-22 10:40:25.700  INFO 39003 --- [           main] o.a.k.clients.producer.KafkaProducer     : [Producer clientId=producer-tx.0, transactionalId=tx.0] Closing the Kafka producer with timeoutMillis = 30000 ms.
> 
> Process finished with exit code 
> ~~~
>
> 在日志中还可以看到生产者和消费者的Kafka配置，如果有配置项需要修改，可以参考Demo1

### 3.4 参考

> * [How to Work with Apache Kafka in Your Spring Boot Application](https://www.confluent.io/blog/apache-kafka-spring-boot-application/)
> * [Spring for Apache Kafka Deep Dive - Part 1: Error Handling, Message Conversion and Transaction Support](https://www.confluent.io/blog/spring-for-apache-kafka-deep-dive-part-1-error-handling-message-conversion-transaction-support/)

### 3.5 相关问题

**多Listener以及多Kafka Template配置**

(1) 让Listener从同一个Kafka集群的多个topic接收不同类型的消息

> 在Demo2中有演示，使用MultiMethodListener

(2) 向同一个Kafka集群的不同topic发送不同类型的消息

> 使用同一个KafkaTemplate向多个topic发送不同消息：KafkaTemplate在调用send时可指定topic，在Demo3中有演示
>
> 装配多个KafkaTemplate，向多个topic发送不同消息：[https://stackoverflow.com/questions/57905042/can-we-use-multiple-kafka-template-in-spring-boot](https://stackoverflow.com/questions/57905042/can-we-use-multiple-kafka-template-in-spring-boot)

(3) 装配多个Listener、让他们订阅不同Kafka集群的消息

> 在Demo1中有演示，可以在[@Configuration类](../spring_kafka_samples/sample-01/src/main/java/com/javaprojref/spring_kafka/pnc_demo/config/KafkaConfig.java)中向不同的Listener传入不同的配置

(4) 装配多个KafkaTemplate，分别向不同的Kafka集群发送消息

> 参考：[https://blog.csdn.net/u010218286/article/details/104897703](https://blog.csdn.net/u010218286/article/details/104897703)
>
> 但是此时容器中有多个KafkaTemplate Bean，虽然对于业务代码可以使用`@Qualifier`注解来选择使用哪个Bean，但对于`Dead Letter Handling`等的框架代码，让它们选择使用该使用哪个KafkaTemplate仍然比较复杂

## 4 在Spring Cloud Stream中使用Kafka

> 参考：https://github.com/fangkun119/manning-smia/blob/master/note/ch10_sprint_cloud_stream.md

