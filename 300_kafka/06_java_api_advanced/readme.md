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
 







