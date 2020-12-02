# Kafka API

## 1. 内容

> 1. Topic基本操作、DML管理
> 2. 生产者
> 3. 消费者及两种模式 sub / assign
> 4. 自定义分区
> 5. 序列化
> 6. 拦截器

## 2. 启动虚拟机、zookeeper、kafka

### (1) 启动虚拟机

> 在三台虚拟机的`Console`中查看`ip add`，得到它们在桥接网卡、HostOnly网卡下的IP地址分别如下
> 
> |  主机名 		| 桥接网卡IP（笔记本访问虚拟机、虚拟机访问外网时使用） | Host Only网卡IP（虚拟机之间互相访问时使用，已配成静态IP）
> |  ---------	| ------------------	| ----------- |
> | CentOSA  	| 192.168.1.124           	| 192.168.56.102 |
> | CentOSB  	| 192.168.1.116            	| 192.168.56.103 |
> | CentOSC 	| 192.168.1.117            	| 192.168.56.104 |

### (2) 启动三台虚拟机上的`zookeeper`

> 下面只列出`CentOSA`命令行记录
> 
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/
> $ ssh root@192.168.1.124
> root@192.168.1.124's password:
> Last login: Sat Nov 28 10:34:17 2020 from 192.168.1.149
> [root@CentOSA ~]# /usr/zookeeper-3.4.6/bin/zkServer.sh start zoo.cfg # 启动
> JMX enabled by default
> Using config: /usr/zookeeper-3.4.6/bin/../conf/zoo.cfg
> Starting zookeeper ... STARTED
> [root@CentOSA ~]# jps #检查进程
> 6113 Jps
> 6087 QuorumPeerMain
> [root@CentOSA ~]# tail -n2 zookeeper.out # 检查4只
> 2020-11-28 11:46:05,914 [myid:1] - INFO  [CentOSA/192.168.56.102:3888:QuorumCnxManager$Listener@511] - Received connection request /192.168.56.104:34362
> 2020-11-28 11:46:05,919 [myid:1] - INFO  [WorkerReceiver[myid=1]:FastLeaderElection@597] - Notification: 1 (message format version), 3 (n.leader), 0x100000105 (n.zxid), 0x1 (n.round), LOOKING (n.state), 3 (n.sid), 0x1 (n.peerEpoch) FOLLOWING (my state)
> ~~~

### (3) 启动三台虚拟机上的`Kafka`

> 下面只列出`CentOSA`的命令行记录
> 
> ~~~bash
> [root@CentOSA ~]# cd /usr/kafka_2.11-2.2.0
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-server-start.sh -daemon config/server.properties #启动
> [root@CentOSA kafka_2.11-2.2.0]# jps #检查进程
> 6529 Kafka
> 6087 QuorumPeerMain
> 6571 Jps
> [root@CentOSA kafka_2.11-2.2.0]# tail -n 2 logs/server.log # 检查日志，篇幅限制用了-n2只打印了最后两行
> [2020-11-28 11:47:58,114] INFO [GroupMetadataManager brokerId=2] Finished loading offsets and group metadata from __consumer_offsets-45 in 0 milliseconds. (kafka.coordinator.group.GroupMetadataManager)
> [2020-11-28 11:47:58,114] INFO [GroupMetadataManager brokerId=2] Finished loading offsets and group metadata from __consumer_offsets-48 in 0 milliseconds. (kafka.coordinator.group.GroupMetadataManager)
> ~~~

### (4) 三个`Kafka`节点都启动后，检查`Kafka`的连通性

> 下面只列出`CentOSA`的命令行记录
> 
> ~~~bash
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --list
> __consumer_offsets
> topic01
> ~~~
demos
## 3. `Demo`代码入口

### (1) 代码

* [`/pom.xml`](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/pom.xml)
* [`/src/main/resources/log4j.properties`](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/src/main/resources/log4j.properties) 
* [`/src/main/java/com/javaproref/kafka/apidemo/Main.java`](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/src/main/java/com/javaproref/kafka/apidemo/Main.java)

### (2) 运行

> 将`mvn package`生成的`jar`包放入之前配置好的笔记本与虚拟机的共享目录
> 
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/300_kafka/demos/ 
> $ cp /Users/fangkun/Dev/git/java_proj_ref/300_kafka/demos/target/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar ~/VirtualBox\ VMs/centos_share/
> ~~~

> 在虚拟机中运行
>
> ~~~bash
> [root@CentOSA ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar
> ~~~

## 4. `Topic`管理

### (1) 代码

> * [`**/dml/KafkaTopicDMLMemo.java`](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/src/main/java/com/javaproref/kafka/apidemo/dml/KafkaTopicDMLMemo.java)
> * [`**/dml/DMLCommon.java`](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/src/main/java/com/javaproref/kafka/apidemo/dml/DMLCommon.java)

### (2) 输出

> ~~~bash
> [root@CentOSA ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar topic_dml
> INFO 2020-11-28 16:20:43,129(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.admin.AdminClientConfig - AdminClientConfig values:
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	client.dns.lookup = default
> 	...
> 	ssl.truststore.type = JKS
> 
> INFO 2020-11-28 16:20:43,688(yyyy-MM-dd HH:mm:ss} org.apache.kafka.common.utils.AppInfoParser - Kafka version: 2.2.0
> INFO 2020-11-28 16:20:43,688(yyyy-MM-dd HH:mm:ss} org.apache.kafka.common.utils.AppInfoParser - Kafka commitId: 05fcfde8f69b0349
> before create topic: dmlTestTopic01
> topic01
> after create topic: dmlTestTopic01
> dmlTestTopic01
> topic01
> dmlTestTopic01	(name=dmlTestTopic01, internal=false, partitions=(partition=0, leader=CentOSA:9092 (id: 2 rack: null), replicas=CentOSA:9092 (id: 2 rack: null), CentOSB:9092 (id: 1 rack: null), isr=CentOSA:9092 (id: 2 rack: null), CentOSB:9092 (id: 1 rack: null)),(partition=1, leader=CentOSB:9092 (id: 1 rack: null), replicas=CentOSB:9092 (id: 1 rack: null), CentOSC:9092 (id: 0 rack: null), isr=CentOSB:9092 (id: 1 rack: null), CentOSC:9092 (id: 0 rack: null)),(partition=2, leader=CentOSC:9092 (id: 0 rack: null), replicas=CentOSC:9092 (id: 0 rack: null), CentOSA:9092 (id: 2 rack: null), isr=CentOSC:9092 (id: 0 rack: null), CentOSA:9092 (id: 2 rack: null)))
> after delete topic: dmlTestTopic01
> topic01
> ~~~
> 
> 另外有两份代码，是为了接下来的实验清理和重置实验环境而编写
> 
> * [**/dml/CreateTestingTopic.java](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/src/main/java/com/javaproref/kafka/apidemo/dml/CreateTestingTopic.java)
> * [**/dml/RemoveTestingTopic.java](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/src/main/java/com/javaproref/kafka/apidemo/dml/RemoveTestingTopic.java)

## 5. 生产者消费者

### (1) 代码

> * [**/producer/KafkaProducerDemo.java](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/src/main/java/com/javaproref/kafka/apidemo/producer/KafkaProducerDemo.java)
> * [**/consumer/ConsumerCommon.java](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/src/main/java/com/javaproref/kafka/apidemo/consumer/ConsumerCommon.java)
> * [**/consumer/KafkaConsumerSubscribeDemo.java](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/src/main/java/com/javaproref/kafka/apidemo/consumer/KafkaConsumerSubscribeDemo.java)

### (2) 实验输出

> 重置实验环境
> 
> ~~~bash
> java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar clear
> java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar init
> ~~~
> 
> Producer Demo
> 
> ~~~bash
> [root@CentOSA ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar producer
> INFO 2020-11-28 17:06:19,850(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.ProducerConfig - ProducerConfig values:
> 	acks = 1
> 	batch.size = 16384
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	...
> 	value.serializer = class org.apache.kafka.common.serialization.StringSerializer
> 
> INFO 2020-11-28 17:06:20,202(yyyy-MM-dd HH:mm:ss} org.apache.kafka.common.utils.AppInfoParser - Kafka version: 2.2.0
> INFO 2020-11-28 17:06:20,202(yyyy-MM-dd HH:mm:ss} org.apache.kafka.common.utils.AppInfoParser - Kafka commitId: 05fcfde8f69b0349
> INFO 2020-11-28 17:06:20,613(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.Metadata - Cluster ID: 8gU3YeKrQaiFufBYCW182g
> INFO 2020-11-28 17:06:20,650(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.KafkaProducer - [Producer clientId=producer-1] Closing the Kafka producer with timeoutMillis = 9223372036854775807 ms.
> ~~~
> 
> Consumer Demo
> 
> ~~~bash
> [root@CentOSA ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar consumer
> INFO 2020-11-28 17:06:25,276(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.ConsumerConfig - ConsumerConfig values:
> 	auto.commit.interval.ms = 5000
> 	auto.offset.reset = latest
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	...
> 	value.deserializer = class org.apache.kafka.common.serialization.StringDeserializer
> 
> INFO 2020-11-28 17:06:25,645(yyyy-MM-dd HH:mm:ss} org.apache.kafka.common.utils.AppInfoParser - Kafka version: 2.2.0
> INFO 2020-11-28 17:06:25,645(yyyy-MM-dd HH:mm:ss} org.apache.kafka.common.utils.AppInfoParser - Kafka commitId: 05fcfde8f69b0349
> INFO 2020-11-28 17:06:25,649(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.KafkaConsumer - [Consumer clientId=consumer-1, groupId=g1] Subscribed to pattern: '^topic.*'
> Type in: Ctrl + C to quit
> INFO 2020-11-28 17:06:29,070(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.Metadata - Cluster ID: 8gU3YeKrQaiFufBYCW182g
> INFO 2020-11-28 17:06:29,075(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.AbstractCoordinator - [Consumer clientId=consumer-1, groupId=g1] Discovered group coordinator CentOSA:9092 (id: 2147483645 rack: null)
> INFO 2020-11-28 17:06:29,136(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-1, groupId=g1] Revoking previously assigned partitions []
> INFO 2020-11-28 17:06:29,137(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.AbstractCoordinator - [Consumer clientId=consumer-1, groupId=g1] (Re-)joining group
> INFO 2020-11-28 17:06:29,146(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.AbstractCoordinator - [Consumer clientId=consumer-1, groupId=g1] (Re-)joining group
> INFO 2020-11-28 17:06:29,168(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.AbstractCoordinator - [Consumer clientId=consumer-1, groupId=g1] Successfully joined group with generation 6
> INFO 2020-11-28 17:06:29,171(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-1, groupId=g1] Setting newly assigned partitions: topic01-2, topic01-1, topic01-0
> topic01	2,6	key_0	value_0	1606554380628
> topic01	2,7	key_6	value_6	1606554380649
> topic01	2,8	key_7	value_7	1606554380649
> topic01	2,9	key_8	value_8	1606554380649
> topic01	2,10	key_9	value_9	1606554380649
> topic01	1,4	key_1	value_1	1606554380647
> topic01	1,5	key_3	value_3	1606554380649
> topic01	0,5	key_2	value_2	1606554380648
> topic01	0,6	key_4	value_4	1606554380649
> topic01	0,7	key_5	value_5	1606554380649
> ^C[root@CentOSA ~]#
> ~~~
> 
> 其中的日志
> 
> ~~~txt
> org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-1, groupId=g1] Setting newly assigned partitions: topic01-2, topic01-1, topic01-0
> ~~~
> 
> 可以看到这个consumer会消费哪几个分区
> 
> * 如果启动多个属于同一个`group`（代码中的`props.put(ConsumerConfig.GROUP_ID_CONFIG, "g1")`）的消费者，这些消费者将分摊负载、分别负责不同的分区。
> 	* 组内新增消费者时、一部分分区将交接给这个消费者
> 	* 组内消费者退出或宕机时、其他消费者将接管这个消费者负责的分区
> * 如果启动多个属于不同`group`的消费者，每条消息将向所有`group`都发送一遍
> * 从消费者打印的消息日志可以看出，只保证同一个partition内的消息有序，来自不同`partition`的消息顺序不保证

## 6. 手动指定`消费者`消费的分区

> 每个消费者消费哪些分区、默认是由`ConsumerCoordinator`来指定，但也可以在消费者的代码中手动指定 

### (1) 代码

> * [**/producer/KafkaProducerDemo.java](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/src/main/java/com/javaproref/kafka/apidemo/producer/KafkaProducerDemo.java)
> * [**/consumer/ConsumerCommon.java](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/src/main/java/com/javaproref/kafka/apidemo/consumer/ConsumerCommon.java)
> * [**/consumer/KafkaConsumerPartitionAssignDemo.java](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/demos/src/main/java/com/javaproref/kafka/apidemo/consumer/KafkaConsumerPartitionAssignDemo.java)

### (2) 实验输出

> 重置实验环境
> 
> ~~~bash
> java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar clear
> java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar init
> ~~~
> 
> Producer生产数据
> 
> ~~~bash
> > [root@CentOSA ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar producer
> INFO 2020-11-28 21:33:59,353(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.ProducerConfig - ProducerConfig values:
> 	acks = 1
> 	batch.size = 16384
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	...
> 	value.serializer = class org.apache.kafka.common.serialization.StringSerializer
> 
> INFO 2020-11-28 21:33:59,872(yyyy-MM-dd HH:mm:ss} org.apache.kafka.common.utils.AppInfoParser - Kafka version: 2.2.0
> INFO 2020-11-28 21:33:59,872(yyyy-MM-dd HH:mm:ss} org.apache.kafka.common.utils.AppInfoParser - Kafka commitId: 05fcfde8f69b0349
> INFO 2020-11-28 21:34:00,166(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.Metadata - Cluster ID: 8gU3YeKrQaiFufBYCW182g
> INFO 2020-11-28 21:34:00,222(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.producer.KafkaProducer - [Producer clientId=producer-1] Closing the Kafka producer with timeoutMillis = 9223372036854775807 ms.
> ~~~
>
> 用手动指定分区的方式让消费者消费，指定其只消费"topic01"的分区0，并且从offset=1开始消费（跳过第一条offset-0的数据）
> 
> ~~~bash
> [root@CentOSA ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar assign_consumer_partition
> INFO 2020-11-28 21:35:13,742(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.ConsumerConfig - ConsumerConfig values:
> 	auto.commit.interval.ms = 5000
> 	auto.offset.reset = latest
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	...
> 	value.deserializer = class org.apache.kafka.common.serialization.StringDeserializer
> 
> INFO 2020-11-28 21:35:14,094(yyyy-MM-dd HH:mm:ss} org.apache.kafka.common.utils.AppInfoParser - Kafka version: 2.2.0
> INFO 2020-11-28 21:35:14,094(yyyy-MM-dd HH:mm:ss} org.apache.kafka.common.utils.AppInfoParser - Kafka commitId: 05fcfde8f69b0349
> INFO 2020-11-28 21:35:14,097(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.consumer.KafkaConsumer - [Consumer clientId=consumer-1, groupId=null] Subscribed to partition(s): topic01-0
> Type in: Ctrl + C to quit
> INFO 2020-11-28 21:35:17,579(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.Metadata - Cluster ID: 8gU3YeKrQaiFufBYCW182g
> topic01	0,1	key_4	value_4	1606570325083
> topic01	0,2	key_5	value_5	1606570325083
> topic01	0,3	key_2	value_2	1606570440220
> topic01	0,4	key_4	value_4	1606570440220
> topic01	0,5	key_5	value_5	1606570440220
> ^C[root@CentOSA ~]#
> ~~~