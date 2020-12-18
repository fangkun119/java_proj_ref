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
> * [kafka/apidemo/Main.java](../demos/src/main/java/com/javaproref/kafka/apidemo/Main.java)

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
> 在`CentOSC`上消费者的日志打印可以看到，虽然生产者重试了三次，但这条消息只被消费了1次（另外的3次被去重）
>
> ~~~bash
> INFO 2020-12-16 21:40:40,849(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-1, groupId=g1] Setting newly assigned partitions: topic01-2, topic01-1, topic01-0
> INFO 2020-12-16 21:40:40,897(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-0 to offset 0.
> INFO 2020-12-16 21:40:40,898(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-2 to offset 0.
> INFO 2020-12-16 21:40:40,917(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-1 to offset 0.
> topic01	0,0	test_ack_key	test_ack	1608126058779
> ~~~
>
> 如果关闭幂等`props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);`，消费者会消费4次

`Kafka`幂等性只能保证一条记录发送的原子性（`一个分区`），但如果保证多条记录（`多分区`）之间的完整性，这个时候需要开启`Kafka`的事务操作

## 5 Kafka事务

### 5.1 `生产者事务Only`与`消费者&生产者事务`

> `Kafka`的事务操作（事务操作同样是在`Kafka 0.11`开始引入），分为：（1）`生产者事务`；（2）`消费者&生产者事务`

#### (1) 生产者事务Only

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_producer_transaction_only.jpg)
>
> 生产者在同一个事物中，发送了3条消息（到3个不同分区），其中1条消息失败，会要求另外2条回滚

#### (2) 消费者&生产者事务

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_cunsumer_producer_trainsaction.jpg)
>
> 图中业务1既是消费者、也是生产者，作为生产者如果它向Topic2发送消息失败，而它上游topic1中对应的消息也不会认为消费成功，状态仍然是`un-committed`

#### (3) 事务隔离级别

背景：为何要为消费者配置事务隔离级别

> 与数据库不同，Kafka采用append的方式将消息写入磁盘文件，当消息发送失败时，并没有办法删除topic中已经写入的消息记录（只能通过状态`uncommitted`和`committed`来表示），因此需要为消费者配置事务隔离级别

配置项：`isolation.level` 

> 默认值：`read_uncommitted`
>
> 如果使用事务，需要将配置改为`isolation.level = read_committed`来阻止消费者消费那些失败事务产生对应的消息

### 5.2 生产者事务

生产者

> 增加关于`transactional-id`，以及幂等性的设置
>
> ```java
> // 在每个事务中，Transaction ID都要求唯一
> props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transaction-id" + UUID.randomUUID().toString());
> // 设置Kafka的重试机制和幂等性
> props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);  // 开启Kafka的幂等特性,它要求ACKS_CONFIG设为true，同时设置RETRIES_CONFIG
> props.put(ProducerConfig.ACKS_CONFIG, "all");               // 要求Kafka把消息同步到所有副本才返回ACK
> props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 20000); // 20000毫秒未收到ACK则认为消息超时
> ```
>
> 同时为了方便观察，也增加了批处理（mini-batch）相关的设置
>
> ~~~java
> // Kafka批处理大小：为了方便观察减小到1024字节
> props.put(ProducerConfig.BATCH_SIZE_CONFIG, 1024);
> // Kafka批处理等待时长：等待5毫秒，如果没有攒够1024字节数据，也会发送
> props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
> ~~~
>
> 最后是事务相关的代码，包含用于触发事务Abort的实验代码
>
> ```java
> // 2 用事务发送消息
> // (1) 事务初始化
> producer.initTransactions();
> try {
>     // (2) 事务开始
>     producer.beginTransaction();
>     for (int i = 0; i < 30; ++i) {
>         if (8 == i) {
>             int j = 10 / 0; // 在发送第8条数据的时候引发一个异常，构造事务终止的场景
>         }
>         ProducerRecord<String, String> record = new ProducerRecord<>(Constants.TOPIC_01, 1/*partition*/, "key " + i, "value " + i);
>         producer.send(record);
>         producer.flush(); // 确保事务总之之前，前7条消息都发出去
>     }
>     // (3) 事务提交
>     producer.commitTransaction();
> } catch (Exception e) {
>     System.out.println("error: " + e.getMessage());
>     // (4) 事务终止
>     producer.abortTransaction();
> }
> ```

消费者

> 容许指定事务隔离级别（`isolation.level`）
>
> ```java
> // 设置消费者的消费事务的隔离级别：read_committed 或 read_uncommitted
> props.put(
>         ConsumerConfig.ISOLATION_LEVEL_CONFIG,
>         enableReadCommitted ? "read_committed" : "read_uncommitted"
>         );
> ```

代码

> * [kafka/tranactions/POnlyTrxDemoConsumer.java](../demos/src/main/java/com/javaproref/kafka/apidemo/tranactions/POnlyTrxDemoConsumer.java)
> * [kafka/tranactions/POnlyTrxDemoProducer.java](../demos/src/main/java/com/javaproref/kafka/apidemo/tranactions/POnlyTrxDemoProducer.java)
> * [kafka/apidemo/Main.java](../demos/src/main/java/com/javaproref/kafka/apidemo/Main.java)
> * [kafka/apidemo/common/ConsumerCommon.java](../demos/src/main/java/com/javaproref/kafka/apidemo/common/ConsumerCommon.java)

运行

> 环境初始化
>
> ~~~bash
> [root@CentOSA ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar clear 2>&1 > out.log
> [root@CentOSA ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar init 2>&1 >> out.log
> [root@CentOSA ~]# tail -n5 out.log
> topics to create:
> (name=topic01, numPartitions=3, replicationFactor=2, replicasAssignments=null, configs=null)
> (name=topic02, numPartitions=3, replicationFactor=2, replicasAssignments=null, configs=null)
> topic01
> topic02
> ~~~
>
> 在一台虚拟机上以`read_committed`的形式启动Consumer，进入监听状态
>
> ~~~bash
> [root@CentOSA ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar prod_trx_consumer_read_committed
> ......
> org.apache.kafka.clients.consumer.ConsumerConfig - ConsumerConfig values:
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	group.id = g1
> 	isolation.level = read_committed
> 	......
> Type in: Ctrl + C to quit
> ~~~
>
> 在另一台虚拟机上启动Producer，在一个事务中发送一批记录，前7条记录发送后在第8条记录时触发异常引发transaction abort
>
> ~~~bash
> [root@CentOSC ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar prod_trx_producer
> INFO 2020-12-17 16:32:45,452(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.ProducerConfig - ProducerConfig values:
> 	acks = all
> 	batch.size = 1024
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	enable.idempotence = true
> 	linger.ms = 5
> 	max.in.flight.requests.per.connection = 5
>   ...
> INFO 2020-12-17 16:32:45,796(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.KafkaProducer - [Producer clientId=producer-1, transactionalId=transaction-idc6bd7b22-b19a-4975-925b-6e8e515e5034] Instantiated a transactional producer.
> INFO 2020-12-17 16:32:45,987(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.KafkaProducer - [Producer clientId=producer-1, transactionalId=transaction-idc6bd7b22-b19a-4975-925b-6e8e515e5034] Overriding the default retries config to the recommended value of 2147483647 since the idempotent producer is enabled.
> ...
> INFO 2020-12-17 16:32:47,505(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.internals.TransactionManager - [Producer clientId=producer-1, transactionalId=transaction-idc6bd7b22-b19a-4975-925b-6e8e515e5034] ProducerId set to 15000 with epoch 0
> error: / by zero
> INFO 2020-12-17 16:32:47,738(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.KafkaProducer - [Producer clientId=producer-1, transactionalId=transaction-idc6bd7b22-b19a-4975-925b-6e8e515e5034] Closing the Kafka producer with timeoutMillis = 9223372036854775807 ms.
> ~~~
>
> 与此同时消费者并不会收到任何来自un-committed事务的脏数据
>
> ~~~bash
> INFO 2020-12-17 16:32:15,053(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.FetchSessionHandler - [Consumer clientId=consumer-1, groupId=g1] Node 2 was unable to process the fetch request with (sessionId=707163606, epoch=418): INVALID_FETCH_SESSION_EPOCH.
> ~~~
>
> 如果消费者当时这设置的不是"read_committed"而是"read_uncommitted"，那么就会消费并输出前7条来自失败事务的脏数据。下面不重复整个实验，只截取Consumer的相关日志
>
> ~~~bash
> [root@CentOSA ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar prod_trx_consumer_read_uncommitted
> INFO 2020-12-17 17:17:08,443(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.ConsumerConfig - ConsumerConfig values:
> 	auto.offset.reset = latest
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	group.id = g1
> 	isolation.level = read_uncommitted
> ...
> INFO 2020-12-17 17:17:08,996(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.KafkaConsumer - [Consumer clientId=consumer-1, groupId=g1] Subscribed to topic(s): topic01
> Type in: Ctrl + C to quit
> ...
> INFO 2020-12-17 17:17:12,611(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-1, groupId=g1] Setting newly assigned partitions: topic01-2, topic01-1, topic01-0
> INFO 2020-12-17 17:17:12,676(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-0 to offset 0.
> INFO 2020-12-17 17:17:12,677(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-2 to offset 0.
> INFO 2020-12-17 17:17:12,677(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-1 to offset 0.
> topic01	1,0	key 0	value 0	1608196638386
> topic01	1,1	key 1	value 1	1608196638501
> topic01	1,2	key 2	value 2	1608196638535
> topic01	1,3	key 3	value 3	1608196638544
> topic01	1,4	key 4	value 4	1608196638551
> topic01	1,5	key 5	value 5	1608196638562
> topic01	1,6	key 6	value 6	1608196638579
> topic01	1,7	key 7	value 7	1608196638605
> ~~~

### 5.3 消费者&生产者事务

(1) 代码

> * [kafka/apidemo/tranactions/CNPTrxDemoProducer.java](../demos/src/main/java/com/javaproref/kafka/apidemo/tranactions/CNPTrxDemoProducer.java)：生产者节点
> * [kafka/apidemo/tranactions/CNPTrxDemoForwardNode.java](../demos/src/main/java/com/javaproref/kafka/apidemo/tranactions/CNPTrxDemoForwardNode.java)：转发节点、既是消费者又是生产者
> * [kafka/apidemo/tranactions/CNPTrxDemoConsumer.java](../demos/src/main/java/com/javaproref/kafka/apidemo/tranactions/CNPTrxDemoConsumer.java)：消费者节点
> * [kafka/apidemo/Main.java](../demos/src/main/java/com/javaproref/kafka/apidemo/Main.java)

(2) 运行

Demo 1：1个`生产者`节点、2个属于同一个消费者组的`转发节点`、1个`消费者`节点；模拟`转发节点`故障导致事务终止的场景

> 清理环境
>
> ~~~bash
> [root@CentOSA share]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar clear 2>&1 >out.log
> [root@CentOSA share]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar init 2>&1 >out.log
> ~~~
>
> 在虚拟机`CentOSB`打开两个终端窗口，一个启动正常的`转发节点`，一个启动会引发事务终止的`转发节点`
>
> 正常的转发节点、作为Consumer它负责了`topic01-2`即`topic01`的`partition 2`
>
> ~~~bash
> [root@CentOSB share]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar cnp_trx_forward_node
> INFO 2020-12-18 14:06:01,303(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.ConsumerConfig - ConsumerConfig values:
> 	auto.offset.reset = latest
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	enable.auto.commit = false
> 	group.id = g1
> 	isolation.level = read_committed
> 	...
> org.apache.kafka.clients.producer.ProducerConfig - ProducerConfig values:
> 	acks = all
> 	batch.size = 1024
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	enable.idempotence = true
> 	linger.ms = 5
> 	max.in.flight.requests.per.connection = 5
> 	...
> 
> INFO 2020-12-18 14:06:02,036(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.KafkaProducer - [Producer clientId=producer-1, transactionalId=transaction-idac6c49ff-9780-4251-96cf-af1ecd55dafc] Instantiated a transactional producer.
> INFO 2020-12-18 14:06:02,131(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.KafkaConsumer - [Consumer clientId=consumer-1, groupId=g1] Subscribed to topic(s): topic01
> Type in: Ctrl + C to quit
> INFO 2020-12-18 14:06:03,276(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.AbstractCoordinator - [Consumer clientId=consumer-1, groupId=g1] Successfully joined group with generation 2
> INFO 2020-12-18 14:06:03,291(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-1, groupId=g1] Setting newly assigned partitions: topic01-2
> INFO 2020-12-18 14:06:03,344(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-2 to offset 0.
> ~~~
>
> 会引发事务终止的转发节点，它在两个`转发节点`都加入后，经过rebalance，最终负责`topic01-0`和`topic01-1`，即`topic01`的`partition 0和1`
>
> ~~~bash
> [root@CentOSB ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar cnp_trx_forward_node_abort
> INFO 2020-12-18 14:05:46,760(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.ConsumerConfig - ConsumerConfig values:
> 	auto.offset.reset = latest
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	enable.auto.commit = false
> 	group.id = g1
> 	isolation.level = read_committed
> 	...
> org.apache.kafka.clients.producer.ProducerConfig - ProducerConfig values:
> 	acks = all
> 	batch.size = 1024
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	enable.idempotence = true
> 	linger.ms = 5
> 	max.in.flight.requests.per.connection = 5
> 	...
> 
> INFO 2020-12-18 14:05:47,492(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.KafkaProducer - [Producer clientId=producer-1, transactionalId=transaction-id2bd12a33-24c5-4c07-96e2-dfe04653911a] Instantiated a transactional producer.
> INFO 2020-12-18 14:05:47,541(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.KafkaConsumer - [Consumer clientId=consumer-1, groupId=g1] Subscribed to topic(s): topic01
> Type in: Ctrl + C to quit
> ...
> org.apache.kafka.clients.consumer.internals.AbstractCoordinator - [Consumer clientId=consumer-1, groupId=g1] Successfully joined group with generation 1
> INFO 2020-12-18 14:05:48,218(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-1, groupId=g1] Setting newly assigned partitions: topic01-2, topic01-1, topic01-0
> INFO 2020-12-18 14:05:48,295(yyyy-MM-dd HH:mm:ss} 
> ...
> INFO 2020-12-18 14:06:03,267(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.AbstractCoordinator - [Consumer clientId=consumer-1, groupId=g1] Attempt to heartbeat failed since group is rebalancing
> ...
> org.apache.kafka.clients.consumer.internals.AbstractCoordinator - [Consumer clientId=consumer-1, groupId=g1] Successfully joined group with generation 2
> INFO 2020-12-18 14:06:03,280(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-1, groupId=g1] Setting newly assigned partitions: topic01-1, topic01-0
> INFO 2020-12-18 14:06:03,511(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-1 to offset 0.
> INFO 2020-12-18 14:06:03,519(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic01-0 to offset 0.
> ~~~
>
> 在虚拟机`Cent0SC`上启动消费者
>
> ~~~bash
> [root@CentOSC ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar cnp_trx_consumer
> INFO 2020-12-18 14:18:27,611(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.ConsumerConfig - ConsumerConfig values:
> 	auto.offset.reset = latest
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	enable.auto.commit = true
> 	group.id = g1
> 	isolation.level = read_committed
> 	...
> 
> INFO 2020-12-18 14:18:28,208(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.KafkaConsumer - [Consumer clientId=consumer-1, groupId=g1] Subscribed to topic(s): topic02
> Type in: Ctrl + C to quit
> ~~~
>
> 在虚拟机`CentOSA`上启动生产者、在一个事务中发送30条数据
>
> ~~~bash
> [root@CentOSA share]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar cnp_trx_producer
> INFO 2020-12-18 14:20:50,498(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.ProducerConfig - ProducerConfig values:
> 	acks = all
> 	batch.size = 1024
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	enable.idempotence = true
> 	linger.ms = 5
> 	max.in.flight.requests.per.connection = 5
> 	...
> INFO 2020-12-18 14:20:50,646(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.KafkaProducer - [Producer clientId=producer-1, transactionalId=transaction-idbc3263e1-e657-4660-886d-4dc51ea2b7a6] Instantiated a transactional producer.
> ...
> ~~~
>
> 在虚拟机`CentOSB`上会引发事务终止的`转发节点`上，可以看到，此时，该事务被认为是失败，事务内的`30条数据`都不能到达`消费者`节点
>
> ~~~bash
> exception: / by zero, producer abort transaction
> ~~~
>
> 如果`CentOSB`上两个`转发节点`都不会引发事务终止，则30条数据都会到达`消费者`节点

Demo 2：1个`生产者`节点、2个属于同一个消费者组的`转发节点`、1个`消费者`节点；模拟`生产者节点`故障导致事务终止的场景

> 在虚拟机`CentOSB`启动`中间节点`：该节点既是`topic01`的消费者、又是`topic02`的生产者、启动后处于阻塞状态，等待来自`topic01`的数据
>
> ~~~bash
> [root@CentOSB ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar cnp_trx_forward_node
> INFO 2020-12-18 12:09:28,673(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.ConsumerConfig - ConsumerConfig values:
> 	auto.offset.reset = latest
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	enable.auto.commit = false
> 	group.id = g1
> 	isolation.level = read_committed
>   ...
> org.apache.kafka.clients.producer.ProducerConfig - ProducerConfig values:
> 	acks = all
> 	batch.size = 1024
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	enable.idempotence = true
> 	linger.ms = 5
> 	max.in.flight.requests.per.connection = 5
>   ...
> INFO 2020-12-18 12:09:29,844(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.KafkaConsumer - [Consumer clientId=consumer-1, groupId=g1] Subscribed to topic(s): topic01
> Type in: Ctrl + C to quit
> ...
> ~~~
>
> 在虚拟机`CentOSC`上启动`消费者`节点，它订阅`topic02`接收来自`中间节点`的数据
>
> ~~~bash
> [root@CentOSC ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar cnp_trx_consumer
> INFO 2020-12-18 12:28:07,463(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.ConsumerConfig - ConsumerConfig values:
> 	auto.offset.reset = latest
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	group.id = g1
> 	isolation.level = read_committed
> 	......
> INFO 2020-12-18 12:28:08,249(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.KafkaConsumer - [Consumer clientId=consumer-1, groupId=g1] Subscribed to topic(s): topic02
> Type in: Ctrl + C to quit
> ~~~
>
> 在虚拟机`CentOSA`上启动`生产者`，通过`topic01`向中间节点发送数据，但是让它在发送途中触发异常导致`事务终止`
>
> ~~~bash
> [root@CentOSA share]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar cnp_trx_producer_abort
> INFO 2020-12-18 12:33:40,412(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.ProducerConfig - ProducerConfig values:
> 	acks = all
> 	batch.size = 1024
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	enable.idempotence = true
> 	linger.ms = 5
> 	max.in.flight.requests.per.connection = 5
>   ...
> INFO 2020-12-18 12:33:40,731(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.KafkaProducer - [Producer clientId=producer-1, transactionalId=transaction-id6db92bd6-35cc-4074-8db3-c95908a4b270] Instantiated a transactional producer.
> INFO 2020-12-18 12:33:41,775(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.internals.TransactionManager - [Producer clientId=producer-1, transactionalId=transaction-id6db92bd6-35cc-4074-8db3-c95908a4b270] ProducerId set to 23000 with epoch 0
> exception: / by zero
> ~~~
>
> 观察`CentOSB`、`CentOSC`，下游节点并没有消费来自已经`abort`的事务的脏数据（因为设置了`read_committed`）
>
> 在虚拟机`CentOSA`上重新启动`生产者`，但这次不触发异常，让事务内的数据完整发送
>
> ~~~bash
> [root@CentOSA share]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar cnp_trx_producer
> ~~~
>
> 在虚拟机`CentOSC`上可以观察到`消费者`接收到的来自`中间节点`转发的数据
>
> ~~~bash
> INFO 2020-12-18 12:29:25,665(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.Fetcher - [Consumer clientId=consumer-1, groupId=g1] Resetting offset for partition topic02-1 to offset 0.
> topic02	1,0	key 0	value 0 forwarded	1608266997094
> topic02	1,1	key 4	value 4 forwarded	1608266997102
> topic02	1,2	key 5	value 5 forwarded	1608266997102
> topic02	1,3	key 7	value 7 forwarded	1608266997102
> topic02	1,4	key 12	value 12 forwarded	1608266997102
> topic02	1,5	key 18	value 18 forwarded	1608266997102
> topic02	1,6	key 25	value 25 forwarded	1608266997103
> topic02	1,7	key 26	value 26 forwarded	1608266997103
> topic02	1,8	key 28	value 28 forwarded	1608266997103
> topic02	2,0	key 1	value 1 forwarded	1608266997102
> topic02	2,1	key 2	value 2 forwarded	1608266997102
> topic02	2,2	key 6	value 6 forwarded	1608266997102
> topic02	2,3	key 8	value 8 forwarded	1608266997102
> topic02	2,4	key 10	value 10 forwarded	1608266997102
> topic02	2,5	key 11	value 11 forwarded	1608266997102
> topic02	2,6	key 19	value 19 forwarded	1608266997102
> topic02	2,7	key 22	value 22 forwarded	1608266997103
> topic02	2,8	key 27	value 27 forwarded	1608266997103
> topic02	2,9	key 29	value 29 forwarded	1608266997103
> topic02	0,0	key 3	value 3 forwarded	1608266997102
> topic02	0,1	key 9	value 9 forwarded	1608266997102
> topic02	0,2	key 13	value 13 forwarded	1608266997102
> topic02	0,3	key 14	value 14 forwarded	1608266997102
> topic02	0,4	key 15	value 15 forwarded	1608266997102
> topic02	0,5	key 16	value 16 forwarded	1608266997102
> topic02	0,6	key 17	value 17 forwarded	1608266997102
> topic02	0,7	key 20	value 20 forwarded	1608266997102
> topic02	0,8	key 21	value 21 forwarded	1608266997103
> topic02	0,9	key 23	value 23 forwarded	1608266997103
> topic02	0,10	key 24	value 24 forwarded	1608266997103
> ~~~



## 6 关闭zookeeper和kafka

在三台虚拟机上都执行

> ~~~bash
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-server-stop.sh # 关闭kafka
> [root@CentOSA kafka_2.11-2.2.0]# cd /usr/zookeeper-3.4.6/
> [root@CentOSA zookeeper-3.4.6]# ./bin/zkServer.sh stop zoo.cfg #关闭zookeeper
> JMX enabled by default
> Using config: /usr/zookeeper-3.4.6/bin/../conf/zoo.cfg
> Stopping zookeeper ... STOPPED
> [root@CentOSA zookeeper-3.4.6]# jps #确认zookeeper和Kafka进程都已经被关闭
> 31763 Jps
> [root@CentOSA zookeeper-3.4.6]# shutdown 0 # 关闭虚拟机
> Connection to 192.168.1.124 closed by remote host.
> Connection to 192.168.1.124 closed.
> ~~~

