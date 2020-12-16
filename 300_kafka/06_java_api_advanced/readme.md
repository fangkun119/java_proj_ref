# Kafka高级API

## 1. 内容

> * Offset自动控制
> * 生产者ACK、重试机制
> * 幂等
> * 事务

## 2. 启动三台虚拟机上的zookeeper和kafka

> 以`CentOSA`为例：
> 
> ~~~bash
> [root@CentOSA ~]# /usr/zookeeper-3.4.6/bin/zkServer.sh start zoo.cfg
> JMX enabled by default
> Using config: /usr/zookeeper-3.4.6/bin/../conf/zoo.cfg
> Starting zookeeper ... STARTED
> [root@CentOSA ~]# jps
> 2064 Jps
> 2041 QuorumPeerMain
> [root@CentOSA ~]# tail -n2 zookeeper.out # 待3台zookeeper都启动后
> 2020-12-02 11:58:51,511 [myid:1] - INFO  [CentOSA/192.168.56.102:3888:QuorumCnxManager$Listener@511] - Received connection request /192.168.56.104:39004
> 2020-12-02 11:58:51,519 [myid:1] - INFO  [WorkerReceiver[myid=1]:FastLeaderElection@597] - Notification: 1 (message format version), 3 (n.leader), 0x2000002d5 (n.zxid), 0x1 (n.round), LOOKING (n.state), 3 (n.sid), 0x2 (n.peerEpoch) FOLLOWING (my state)
> [root@CentOSA ~]# cd /usr/kafka_2.11-2.2.0
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-server-start.sh -daemon config/server.properties # 启动
> [root@CentOSA kafka_2.11-2.2.0]# jps
> 2402 Kafka
> 2482 Jps
> 2041 QuorumPeerMain
> [root@CentOSA kafka_2.11-2.2.0]#  ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --list # 待3台kafka都启动后
> __consumer_offsets
> topic01
> ~~~

## 2. 消费者组消费偏移量`Offset`

### (1) 新订阅`TOPIC`消费者组的起始偏移量

文档：[http://kafka.apache.org/documentation.html](http://kafka.apache.org/documentation.html)

新订阅的消费者（没有消费记录），从哪条记录开始消费， 配置项为`auto.offset.reset`，配置值如下

> * `latest`：如果未找到先前的偏移量、从最新的记录开始消费
> * `earliest`：如果未找到先前的偏移量、从最早的记录开始消费
> * `none`：如果未找到消费者组先前的偏移量，则想消费者抛出异常

代码：

> * [apidemo/offsets/KafkaOffsetResetDemo.java](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/src/main/java/com/javaproref/kafka/apidemo/offsets/AutoOffsetResetConfigDemo.java) 
> * [apidemo/Main.java](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/src/main/java/com/javaproref/kafka/apidemo/Main.java)

部署：

> 将`mvn package`打包好的`jar`包拷贝到环境搭建时配好的（主机、虚拟机）共享目录中
> 
> ~~~bash
> $ cp /Users/fangkun/Dev/git/java_proj_ref/300_kafka/demos/target/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar ~/VirtualBox\ VMs/centos_share/
> ~~~

测试`auto.offset.reset=latest`：

> 删除并新建`topic01`
> 
> ~~~bash
> [root@CentOSA kafka_2.11-2.2.0]# cd /usr/kafka_2.11-2.2.0/
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --delete --topic topic01
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --create --topic topic01 --partitions 3 --replication-factor 2
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --list
> __consumer_offsets
> topic01
> ~~~
> 
> 用[`KafkaProducerDemo.java`](../demos/src/main/java/com/javaproref/kafka/apidemo/producer/KafkaProducerDemo.java)向`topic01`发送`10`条消息
> 
> ~~~bash
> [root@CentOSA kafka_2.11-2.2.0]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar producer
> ~~~
> 
> 以`auto.offset.reset=latest`的方式让Consumer订阅`topic01`，在日志中可以看到Consumer把每个partition的offset都设置为分区末尾（日志中的2、3、5），加起来和刚好为10
> 
> ~~~bash
> > [root@CentOSA kafka_2.11-2.2.0]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar offset_latest
> INFO 2020-12-02 14:20:55,057(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.ConsumerConfig - ConsumerConfig values:
> 	auto.offset.reset = latest
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	group.id = g1
> 	...
> INFO 2020-12-02 14:20:55,493(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.KafkaConsumer - [Consumer clientId=consumer-1, groupId=g1] Subscribed to topic(s): topic01
> Type in: Ctrl + C to quit
> ...
> INFO 2020-12-02 14:20:59,066(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-1, groupId=g1] Setting newly assigned partitions: topic01-2, topic01-1, topic01-0
> INFO 2020-12-02 14:20:59,123(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-1 to offset 2.
> INFO 2020-12-02 14:20:59,124(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-2 to offset 5.
> INFO 2020-12-02 14:20:59,161(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-0 to offset 3.
> ^C
> ~~~

测试`auto.offset.reset=earlist`：

> ~~~bash
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --delete --topic topic01
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --create --topic topic01 --partitions 3 --replication-factor 2
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --list
> __consumer_offsets
> topic01
> [root@CentOSA kafka_2.11-2.2.0]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar producer 2>&1 >output.log
> [root@CentOSA kafka_2.11-2.2.0]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar offset_earliest
> INFO 2020-12-02 14:37:58,539(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.ConsumerConfig - ConsumerConfig values:
> 	auto.offset.reset = earliest
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	group.id = g1
> 	...
> org.apache.kafka.clients.consumer.KafkaConsumer - [Consumer clientId=consumer-1, groupId=g1] Subscribed to topic(s): topic01
> Type in: Ctrl + C to quit
> INFO 2020-12-02 14:38:02,277(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.AbstractCoordinator - [Consumer clientId=consumer-1, groupId=g1] Discovered group coordinator CentOSA:9092 (id: 2147483645 rack: null)
> ...
> org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-1, groupId=g1] Setting newly assigned partitions: topic01-2, topic01-1, topic01-0
> INFO 2020-12-02 14:38:02,394(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-1 to offset 0.
> INFO 2020-12-02 14:38:02,395(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-0 to offset 0.
> INFO 2020-12-02 14:38:02,395(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-2 to offset 0.
> topic01	2,0	key_0	value_0	1606891072047
> topic01	2,1	key_6	value_6	1606891072073
> topic01	2,2	key_7	value_7	1606891072073
> topic01	2,3	key_8	value_8	1606891072073
> topic01	2,4	key_9	value_9	1606891072073
> topic01	1,0	key_1	value_1	1606891072072
> topic01	1,1	key_3	value_3	1606891072073
> topic01	0,0	key_2	value_2	1606891072073
> topic01	0,1	key_4	value_4	1606891072073
> topic01	0,2	key_5	value_5	1606891072073
> ~~~

`auto.offset.reset`配置项仅在消费者第一次加入到TOPIC中时，才起作用；重新加入TOPIC的消费者，会从上一次的offset位置开始接续接收。这也是上述实验需要删除并重建topic才能看出`auto.offset.reset`配置效果的原因

### (2) `Kafka`如何获取消费者的`offset`状态

消费者默认会定义自动提交`offset`给`Kafka`，这样可以保证消息可以至少被消费一次，以下是相关的消费者配置

> ~~~txt
> enable.auto.commit = true # 是否开启offset自动提交、默认为true
> auto.commit.interval.ms = 5000 # 自动提交间隔、默认为5000
> ~~~

代码：

> * [apidemo/offsets/KafkaOffsetResetDemo.java](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/src/main/java/com/javaproref/kafka/apidemo/offsets/AutoOffsetResetConfigDemo.java) 

### (3) 由消费者管理`offset` 

使用上面的配置关闭`offset自动提交`之后，可以在消费者代码中调用API手动提交`offset`，注意提交的offset值比当前消费的offset值大1，因为`offset`代表着即将消费的下一条记录

代码：

> * [apidemo/offsets/OffsetConsumerSubmitDemo.java](../demos/src/main/java/com/javaproref/kafka/apidemo/offsets/OffsetConsumerSubmitDemo.java)
> * [apidemo/common/ConsumerCommon.java](../demos/src/main/java/com/javaproref/kafka/apidemo/common/ConsumerCommon.java)

测试：

> ~~~bash
> [root@CentOSA kafka_2.11-2.2.0]./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --delete --topic topic01
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --create --topic topic01 --partitions 3 --replication-factor 2
> [root@CentOSA kafka_2.11-2.2.0]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar producer 2>&1 >out.log
> [root@CentOSA kafka_2.11-2.2.0]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar offset_consumer_submit
> ...
> topic01	1,0	key_1	value_1	1606896432445
> topic01	1,1	key_3	value_3	1606896432445
> topic01	0,0	key_2	value_2	1606896432445
> topic01	0,1	key_4	value_4	1606896432445
> topic01	0,2	key_5	value_5	1606896432445
> topic01	2,0	key_0	value_0	1606896432420
> topic01	2,1	key_6	value_6	1606896432445
> topic01	2,2	key_7	value_7	1606896432445
> topic01	2,3	key_8	value_8	1606896432445
> topic01	2,4	key_9	value_9	1606896432445
> offsets: 1	 exception: null
> offsets: 2	 exception: null
> offsets: 1	 exception: null
> offsets: 2	 exception: null
> offsets: 3	 exception: null
> offsets: 1	 exception: null
> offsets: 2	 exception: null
> offsets: 3	 exception: null
> offsets: 4	 exception: null
> offsets: 5	 exception: null
> ~~~

## 3 `Acks`和`Retries`

### 3.1 配置

#### (1) `acks`配置级别

**`acks=1`（默认配置）**：`Leader`将收到的record写到本地日志中之后（不等待`Follower`确认）立即ACK

> 如果`Leader`在ACK之后立即失效、同时`Follower`还没有复制记录，这条记录会丢失（Kafka写丢失的原因之一）

**`acks=0`**：生产者不会等待任何ACK

> record进入生产者socket缓存即视为发送，不保证服务器已收到记录

**`acks=all`**（等效于**`acks=-1`**）：`Leader`等待全套同步副本确认记录

> 这保证只要至少一个同步副本仍处于活动状态，记录就不会丢 失

如果发生ACK丢失（数据写入Kafka分区文件，但是生产者没有收到ACK并重试），会导致Kafka的分区文件中产生重复记录

#### (2) `retries`

如果生产者在规定的时间内没有得到`Kafka`的`Leader`的ACK应答，`Kafka`可以开启retries机制，默认配置是：`request.timeout.ms = 30000`，`retries=2147483647` (int最大值)

### 3.2 Demo

为Producer设置如下参数，使其出现ACK超时重发的情况，来观察数据的发送状况

> ```java
> // 设置Kafka Acks以及Retries
> props.put(ProducerConfig.ACKS_CONFIG, ackCfg);          // ack方式由外部传入
> props.put(ProducerConfig.RETRIES_CONFIG, 3);            // 最多重试3次（加上初始发送总共4次）
> props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 1); // 设置足够小的超时时间，让Producer在收到ACK之间就超时重试
> ```

代码：

> * [kafka/apidemo/acks/AckTimeoutDemoProducer.java](../demos/src/main/java/com/javaproref/kafka/apidemo/acks/AckTimeoutDemoProducer.java)
> * [kafka/apidemo/acks/AckDemoConsumer.java](../demos/src/main/java/com/javaproref/kafka/apidemo/acks/AckDemoConsumer.java)
> * [kafka/apidemo/Main.java](../demos/src/main/java/com/javaproref/kafka/apidemo/Main.java)

运行：

> 环境清理
>
> ~~~bash
> [root@CentOSB kafka_2.11-2.2.0]#java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar clear 2>&1 >output.log
> [root@CentOSB kafka_2.11-2.2.0]#java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar init 2>&1 >output.log
> ~~~
>
> 在`CentOSC`上启动消费者
>
> ~~~bash
> [root@CentOSC ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar ack_consumer
> ......
> org.apache.kafka.clients.consumer.ConsumerConfig - ConsumerConfig values:
> 	auto.offset.reset = latest
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	group.id = g1
> 	......
> org.apache.kafka.clients.consumer.KafkaConsumer - [Consumer clientId=consumer-1, groupId=g1] Subscribed to topic(s): topic01
> Type in: Ctrl + C to quit
> ~~~
>
> 在`CentOSA`上启动生产者为这条消息一共重试了3次
>
> ~~~bash
> [root@CentOSA share]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar ack_timeout_producer
> INFO 2020-12-16 16:32:42,880(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.ProducerConfig - ProducerConfig values:
> 	acks = all
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	request.timeout.ms = 1
> 	retries = 3
> 	...
> WARN 2020-12-16 16:32:44,084(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.internals.Sender - [Producer clientId=producer-1] Got error produce response with correlation id 7 on topic-partition topic01-0, retrying (2 attempts left). Error: NETWORK_EXCEPTION
> ...
> WARN 2020-12-16 16:32:44,251(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.internals.Sender - [Producer clientId=producer-1] Got error produce response with correlation id 15 on topic-partition topic01-0, retrying (1 attempts left). Error: NETWORK_EXCEPTION
> ...
> WARN 2020-12-16 16:32:44,358(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.internals.Sender - [Producer clientId=producer-1] Got error produce response with correlation id 18 on topic-partition topic01-0, retrying (0 attempts left). Error: NETWORK_EXCEPTION
> ...
> INFO 2020-12-16 16:32:44,467(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.KafkaProducer - [Producer clientId=producer-1] Closing the Kafka producer with timeoutMillis = 9223372036854775807 ms.
> ~~~
> 
>与此同时在`CentOSC`上消费者的日志打印可以看到，这条消息一共被消费者消费了4次（1次正常发送，外加3次重试）
> 
>~~~bash
> Type in: Ctrl + C to quit
> ...
> INFO 2020-12-16 16:29:58,558(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-2 to offset 0.
> INFO 2020-12-16 16:29:58,723(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-0 to offset 0.
> INFO 2020-12-16 16:29:58,743(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-1 to offset 0.
> topic01	0,0	test_ack_key	test_ack	1608107564013
> topic01	0,1	test_ack_key	test_ack	1608107564013
> topic01	0,2	test_ack_key	test_ack	1608107564013
> topic01	0,3	test_ack_key	test_ack	1608107564013
> ~~~

问题：

> * `重复发送`：对于订单之类的数据、不考虑`重复发送`会导致"重复下单"等问题
> * `发送ACK给生产者、但是在写入副本之前宕机`：会导致丢数据
>
> 这两个问题需要靠“幂等写”和“生产者事务”来解决

## 4 `幂等写`

### 4.1 Kafka的幂等写

> **幂等**又称为`excctly once`， `HTTP/1.1`中的定义为一次和多次请某个资源，对于资源本身有同样的结果。`Kafka `在0.11.0.0版本增加了对幂等的支持（针对生产者角度）：
>
> * 保证生产者发送的消息**不会重复**。
> * 实现的关键在于可以发现重复请求并过滤掉重复请求，通过为请求增加**唯一表示**（例如支付请求、订单号就是唯一标识）并记录已经处理过的请求来实现
>
> 具体的实现机制：
>
> * `Kafka`为生产者生成唯一的`producer id`(`PID`)
> * `PID`和`消息序列号`捆绑在一起组成消息的唯一表示，序列号从0开始递增
> * `Broker`收到消息时，仅当该消息的序列号比该`<PID,TopicPartition> Pair`中最好提交的消息大1时，Broker才接受该消息

### 4.2 配置

> * `enable.idempotence`
>   * 默认值时false
>   * 配为true时、要求必须开启`retries = true`和`asks = all`
> * `max.in.flight.requests.per.connection`
>   * 是一个与`idempotence`一起用的配置，表示容许有多少个未经ACK的请求在同时进行
>   * 配置值不应超过5，值大于1时能增加吞吐量，但是会导致下游收到的数据乱序

### 4.2 Demo

为producer增加如下配置

> ```java
> props.put(ProducerConfig.ACKS_CONFIG, "all");           // 要求Kafka把消息同步到所有副本才返回ACK
> props.put(ProducerConfig.RETRIES_CONFIG, 3);            // 最多重试3次（加上初始发送总共4次）
> props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 1); // 设置足够小的超时时间，让Producer在收到ACK之间就超时重试
> // 开启Kafka的幂等特性,它要求ACKS_CONFIG设为true，同时设置RETRIES_CONFIG
> props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
> // 允许有多少个未经ACK的请求在同时发送（异步发送），最大值不超过5，值大于1时能增加吞吐量但是会导致下游接收消息时乱序
> props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
> ```

代码 

> * [kafka/apidemo/acks/IdempotenceDemoProducer.java](../demos/src/main/java/com/javaproref/kafka/apidemo/acks/IdempotenceDemoProducer.java)
> * [kafka/apidemo/acks/AckDemoConsumer.java](../demos/src/main/java/com/javaproref/kafka/apidemo/acks/AckDemoConsumer.java)
> * [kafka/apidemo/Main.java)](../demos/src/main/java/com/javaproref/kafka/apidemo/Main.java)

运行

> 环境清理
>
> ~~~bash
> [root@CentOSB kafka_2.11-2.2.0]#java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar clear 2>&1 >output.log
> [root@CentOSB kafka_2.11-2.2.0]#java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar init 2>&1 >output.log
> ~~~
>
> 在`CentOSC`上启动消费者，进入阻塞状态等待生产者发送数据
>
> ~~~bash
> [root@CentOSC ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar ack_consumer
> ......
> org.apache.kafka.clients.consumer.ConsumerConfig - ConsumerConfig values:
> 	auto.offset.reset = latest
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	group.id = g1
> 	......
> org.apache.kafka.clients.consumer.KafkaConsumer - [Consumer clientId=consumer-1, groupId=g1] Subscribed to topic(s): topic01
> Type in: Ctrl + C to quit
> ~~~
>
> 在`CentOSA`上启动生产者，日志中可以看到生产者重试了3次
>
> ~~~bash
> [root@CentOSA ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar act_idempotence_producer
> INFO 2020-12-16 21:40:56,540(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.ProducerConfig - ProducerConfig values:
> 	acks = all
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	max.in.flight.requests.per.connection = 1
> 	request.timeout.ms = 1
> 	retries = 3
> 	...
> 
> WARN 2020-12-16 21:41:02,816(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.internals.Sender - [Producer clientId=producer-1] Got error produce response with correlation id 44 on topic-partition topic01-0, retrying (2 attempts left). Error: NETWORK_EXCEPTION
> ...
> WARN 2020-12-16 21:41:02,919(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.internals.Sender - [Producer clientId=producer-1] Got error produce response with correlation id 48 on topic-partition topic01-0, retrying (1 attempts left). Error: NETWORK_EXCEPTION
> ...
> WARN 2020-12-16 21:41:03,354(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.internals.Sender - [Producer clientId=producer-1] Got error produce response with correlation id 53 on topic-partition topic01-0, retrying (0 attempts left). Error: NETWORK_EXCEPTION
> WARN 2020-12-16 21:41:03,355(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.internals.Sender - [Producer clientId=producer-1] Received invalid metadata error in produce request on partition topic01-0 due to org.apache.kafka.common.errors.NetworkException: The server disconnected before a response was received.. Going to request ...
> ~~~
>
> 在`CentOSC`上消费者的日志打印可以看到，虽然生产者重试了三次，但这条消息只被消费了一次
>
> ~~~bash
> INFO 2020-12-16 21:40:40,849(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-1, groupId=g1] Setting newly assigned partitions: topic01-2, topic01-1, topic01-0
> INFO 2020-12-16 21:40:40,897(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-0 to offset 0.
> INFO 2020-12-16 21:40:40,898(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-2 to offset 0.
> INFO 2020-12-16 21:40:40,917(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-1 to offset 0.
> topic01	0,0	test_ack_key	test_ack	1608126058779
> ~~~

