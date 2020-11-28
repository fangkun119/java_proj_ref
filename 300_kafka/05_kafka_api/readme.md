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

## 3. Topic管理及DML

### (1) 代码

* [`/pom.xml`](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/05_kafka_api/kafka_api_demo01/pom.xml)
* [`/src/main/resources/log4j.properties`](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/05_kafka_api/kafka_api_demo01/src/main/resources/log4j.properties) 
* [`/src/main/java/com/javaproref/kafka/apidemo/dml/KafkaTopicDML.java`](https://github.com/fangkun119/java_proj_ref/blob/master/300_kafka/05_kafka_api/kafka_api_demo01/src/main/java/com/javaproref/kafka/apidemo/dml/KafkaTopicDML.java)

### (2) 运行

> 将`mvn package`生成的`jar`包放入笔记本与虚拟机的共享目录中
> 
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/300_kafka/05_kafka_api/kafka_api_demo01/ 
> $ cp /Users/fangkun/Dev/git/java_proj_ref/300_kafka/05_kafka_api/kafka_api_demo01/target/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar ~/VirtualBox\ VMs/centos_share/
> ~~~

> 在虚拟机中运行
>
> ~~~bash
> [root@CentOSA ~]# java -jar ~/share/kafka_mq_demo01-1.0-SNAPSHOT-jar-with-dependencies.jar
> INFO 2020-11-28 14:29:29,722(yyyy-MM-dd HH:mm:ss} org.apache.kafka.clients.admin.AdminClientConfig - AdminClientConfig values:
> 	bootstrap.servers = [CentOSA:9092, CentOSB:9092, CentOSC:9092]
> 	client.dns.lookup = default
> 	...
> 	ssl.truststore.type = JKS
> 
> INFO 2020-11-28 14:29:30,026(yyyy-MM-dd HH:mm:ss} org.apache.kafka.common.utils.AppInfoParser - Kafka version: 2.2.0
> INFO 2020-11-28 14:29:30,026(yyyy-MM-dd HH:mm:ss} org.apache.kafka.common.utils.AppInfoParser - Kafka commitId: 05fcfde8f69b0349
> before create topic: dmlTestTopic01
> topic01
> after create topic: dmlTestTopic01
> dmlTestTopic01
> topic01
> dmlTestTopic01	(name=dmlTestTopic01, internal=false, partitions=(partition=0, leader=CentOSC:9092 (id: 0 rack: null), replicas=CentOSC:9092 (id: 0 rack: null), CentOSA:9092 (id: 2 rack: null), isr=CentOSC:9092 (id: 0 rack: null), CentOSA:9092 (id: 2 rack: null)),(partition=1, leader=CentOSA:9092 (id: 2 rack: null), replicas=CentOSA:9092 (id: 2 rack: null), CentOSB:9092 (id: 1 rack: null), isr=CentOSA:9092 (id: 2 rack: null), CentOSB:9092 (id: 1 rack: null)),(partition=2, leader=CentOSB:9092 (id: 1 rack: null), replicas=CentOSB:9092 (id: 1 rack: null), CentOSC:9092 (id: 0 rack: null), isr=CentOSB:9092 (id: 1 rack: null), CentOSC:9092 (id: 0 rack: null)))
> after delete topic: dmlTestTopic01
> topic01
> ~~~