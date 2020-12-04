# Kafka TOPIC管理

## 1. 启动Kafka集群

(1) 启动上一个实验[`Kafaka集群安装`](https://github.com/fangkun119/java_proj_ref/tree/master/300_kafka/03_cluster_setup)搭建的3台虚拟机（`CentOSA`，`CentOSB`，`CentOSC`），并登入三台虚拟机

(2) 启动三台虚拟机上的zookeeper节点、以`CentOSA`为例：

> ~~~bash
> [root@CentOSA ~]# /usr/zookeeper-3.4.6/bin/zkServer.sh start zoo.cfg #启动
> JMX enabled by default
> Using config: /usr/zookeeper-3.4.6/bin/../conf/zoo.cfg
> Starting zookeeper ... STARTED
> [root@CentOSA ~]# jps #检查进程是否启动
> 6745 Jps
> 6095 QuorumPeerMain
> [root@CentOSA ~]# tail zookeeper.out # 检查日志
> 2020-11-27 16:38:09,160 [myid:1] - INFO  [QuorumPeer[myid=1]/0:0:0:0:0:0:0:0:2181:Learner@323] - Getting a diff from the leader 0x0
> 2020-11-27 16:38:09,168 [myid:1] - INFO  [QuorumPeer[myid=1]/0:0:0:0:0:0:0:0:2181:FileTxnSnapLog@240] - Snapshotting: 0x0 to /root/zkdata/version-2/snapshot.0
> 2020-11-27 16:38:10,556 [myid:1] - INFO  [CentOSA/192.168.56.102:3888:QuorumCnxManager$Listener@511] - Received connection request /192.168.56.104:53134
> 2020-11-27 16:38:10,560 [myid:1] - INFO  [WorkerReceiver[myid=1]:FastLeaderElection@597] - Notification: 1 (message format version), 3 (n.leader), 0x0 (n.zxid), 0x1 (n.round), LOOKING (n.state), 3 (n.sid), 0x0 (n.peerEpoch) FOLLOWING (my state)
> ~~~

(3) 启动三台虚拟机上的`Kafka`节点、以`CentOSA`为例：

> ~~~bash
> [root@CentOSA kafka_2.11-2.2.0]# cd /usr/kafka_2.11-2.2.0
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-server-start.sh -daemon config/server.properties
> [root@CentOSA kafka_2.11-2.2.0]# jps #检查进程
> 8593 Kafka
> 8625 Jps
> 6095 QuorumPeerMain
> [root@CentOSA kafka_2.11-2.2.0]# tail -n 2 logs/server.log # 检查日志，篇幅限制用了-n2只打印了最后两行
> [2020-11-27 17:02:19,540] INFO Kafka commitId: 05fcfde8f69b0349 (org.apache.kafka.common.utils.AppInfoParser)
> [2020-11-27 17:02:19,541] INFO [KafkaServer id=2] started (kafka.server.KafkaServer)
> ~~~

(4) 等待三个节点都已加入

> ~~~bash
> > [root@CentOSA kafka_2.11-2.2.0]#  ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:909 --list  # 对刚启动的集群、需要等待3个节点都已经加入，执行这条命令不会报某个节点无法连接的Warning
> > 
> ~~~

## 2. TOPIC管理

### (1) 创建TOPIC：`--create`

> 创建名为"topic01"的Topic，分区数为3（不能大于节点数），每个分区2副本（不能大于节点数）
> 
> ~~~bash
> [root@CentOSA kafka_2.11-2.2.0]# cd /usr/kafka_2.11-2.2.0
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --create --topic topic01 --partitions 3 --replication-factor 2
> [root@CentOSA kafka_2.11-2.2.0]#
> ~~~

### (2) 查看TOPIC：`--list`，`--describe` 

列出集群中的topic

> ~~~bash
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:909 --list
> topic01
> ~~~

查看topic详细信息

> ~~~bash
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --describe --topic topic01
> Topic:topic01	PartitionCount:3	ReplicationFactor:2	Configs:segment.bytes=1073741824
> 	Topic: topic01	Partition: 0	Leader: 2	Replicas: 2,1	Isr: 2,1
> 	Topic: topic01	Partition: 1	Leader: 1	Replicas: 1,0	Isr: 1,0
> 	Topic: topic01	Partition: 2	Leader: 0	Replicas: 0,2	Isr: 0,2
> ~~~

查看topic的文件，可以看到每个节点存储3个分区中的2个，每个分区有2个副本分别位于2个不同的节点上

> `CentOSA`
> 
> ~~~bash
> [root@CentOSA kafka_2.11-2.2.0]# ls /usr/kafka-logs/
> cleaner-offset-checkpoint  log-start-offset-checkpoint  meta.properties  recovery-point-offset-checkpoint  replication-offset-checkpoint  topic01-0  topic01-2
> ~~~
> 
> `CentOSB`
> 
> ~~~bash
> [root@CentOSB kafka_2.11-2.2.0]#  ls /usr/kafka-logs/
> cleaner-offset-checkpoint  log-start-offset-checkpoint  meta.properties  recovery-point-offset-checkpoint  replication-offset-checkpoint  topic01-0  topic01-1
> ~~~
> 
> `CentOSC`
> 
> ~~~bash
> [root@CentOSC kafka_2.11-2.2.0]#  ls /usr/kafka-logs/
> cleaner-offset-checkpoint  log-start-offset-checkpoint  meta.properties  recovery-point-offset-checkpoint  replication-offset-checkpoint  topic01-1  topic01-2
> ~~~

### (3) 修改TOPIC：`--alter`

> 分区数只能增、不能减
> 
> ~~~bash
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --create --topic topic02 --partitions 2 --replication-factor 3
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --describe --topic topic02
> Topic:topic02	PartitionCount:2	ReplicationFactor:3	Configs:segment.bytes=1073741824
> 	Topic: topic02	Partition: 0	Leader: 0	Replicas: 0,1,2	Isr: 0,1,2
> 	Topic: topic02	Partition: 1	Leader: 2	Replicas: 2,0,1	Isr: 2,0,1
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --alter --topic topic02 --partitions 3
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --describe --topic topic02
> Topic:topic02	PartitionCount:3	ReplicationFactor:3	Configs:segment.bytes=1073741824
> 	Topic: topic02	Partition: 0	Leader: 0	Replicas: 0,1,2	Isr: 0,1,2
> 	Topic: topic02	Partition: 1	Leader: 2	Replicas: 2,0,1	Isr: 2,0,1
> 	Topic: topic02	Partition: 2	Leader: 2	Replicas: 2,0,1	Isr: 2,0,1
> ~~~

### (4) 删除TOPIC：`--delete`

> ~~~bash
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --delete --topic topic02
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --delete --topic topic01
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --list
> 
> [root@CentOSA kafka_2.11-2.2.0]#
> [root@CentOSA kafka_2.11-2.2.0]# ls /usr/kafka-logs/ # 
> cleaner-offset-checkpoint   meta.properties  replication-offset-checkpoint  topic01-2.ed97dd871a1b4afa945af29be4c5265b-delete log-start-offset-checkpoint  recovery-point-offset-checkpoint  topic01-0.992c02a25f6c4d6dab2bbe5101439435-delete
> ~~~

### (5) TOPIC订阅和发布：`kafka-console-consumer.sh`、`kafka-console-producer.sh`

之前创建的所有`topic`在上面的操作中都已经被删除了，因此先创建`topic`

> ~~~bash
> > [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --create --topic topic01 --partitions 3 --replication-factor 3
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-topics.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --list
> topic01
> ~~~

在`CentOSA`上启动消费者，让它订阅`topic01`，其中`--property print.key=true --property print.value=true --property key.separator=,`是可选参数，默认为`false`（不打印key、value）

> ~~~bash
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-console-consumer.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --topic topic01 --group g1 --property print.key=true --property print.value=true --property key.separator=,
> 
> ~~~

在`CentOSB`上启动生产者，让它向`topic01`发送消息

> ~~~bash
> [root@CentOSB kafka_2.11-2.2.0]# ./bin/kafka-console-producer.sh --broker-list CentOSA:9092,CentOSB:9092,CentOSC:9092 --topic topic01
> >message01
> >message02
> >this is the third message
> >hello world
> >the 5th message
> ~~~

与此同时、先前阻塞等消息的消费者`CentOSA`也开始打印收到的数据（因为`kafka-console-producer.sh`无法添加key，因此打印出来的key是null）

> ~~~bash
> [root@CentOSA kafka_2.11-2.2.0]# ./bin/kafka-console-consumer.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --topic topic01 --group g1 --property print.key=true --property print.value=true --property key.separator=,
> null,message01
> null,message02
> null,this is the third message
> null,hello world
> null,the 5th message
> ~~~

### (6) Kafka Consumer Group

列出所有Consumer Group 

> ~~~bash
> [root@CentOSC kafka_2.11-2.2.0]# ./bin/kafka-consumer-groups.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --list
> g1
> ~~~

打印某个Consumer Group的详细信息，其中`CURRENT-OFFSET`、`LOG-END-OFFSET`、`LAG`分别是分区的消费当前位置、写入位置、两个位置的差值

> ~~~bash
> [root@CentOSC kafka_2.11-2.2.0]# ./bin/kafka-consumer-groups.sh --bootstrap-server CentOSA:9092,CentOSB:9092,CentOSC:9092 --describe --group g1
> TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                     HOST            CLIENT-ID
> topic01         0          2               2               0               consumer-1-ef2c043e-7135-47be-bd7e-448dcc42ea6d /192.168.56.102 consumer-1
> topic01         1          2               2               0               consumer-1-ef2c043e-7135-47be-bd7e-448dcc42ea6d /192.168.56.102 consumer-1
> topic01         2          1               1               0               consumer-1-ef2c043e-7135-47be-bd7e-448dcc42ea6d /192.168.56.102 consumer-1
> ~~~

## 3. 关闭zookeeper和kafka

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
> Shutdown scheduled for 五 2020-11-27 21:16:08 CST, use 'shutdown -c' to cancel.
> [root@CentOSA zookeeper-3.4.6]# Connection to 192.168.1.124 closed by remote host.
> Connection to 192.168.1.124 closed.
> ~~~

