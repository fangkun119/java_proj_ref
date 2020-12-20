# Kafka其他主题

## 1 高水位（High Water Mark)

背景：0.11版本之前的Kafka使用高水位机制来维护Leader和Follower之间的数据同步状态

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
>   ![](https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_dataloss_s1.jpg)  
>
> * Follower与Leader同时重启，先启动的Follower（截断尚未恢复）接收新数据时，引发数据不一致的问题（[Replica Divergence on Restart after Multiple Hard Failures](https://cwiki.apache.org/confluence/display/KAFKA/KIP-101+-+Alter+Replication+Protocol+to+use+Leader+Epoch+rather+than+High+Watermark+for+Truncation#KIP101AlterReplicationProtocoltouseLeaderEpochratherthanHighWatermarkforTruncation-Scenario2:ReplicaDivergenceonRestartafterMultipleHardFailures)）
>
>   ![](https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_replica_divergence_s1.jpg)

`Leader Epoch`机制（Kafka 0.11版本开始）：

> Kafka 0.11版本引入[`Leader Epoch`机制](https://cwiki.apache.org/confluence/display/KAFKA/KIP-101+-+Alter+Replication+Protocol+to+use+Leader+Epoch+rather+than+High+Watermark+for+Truncation#KIP101AlterReplicationProtocoltouseLeaderEpochratherthanHighWatermarkforTruncation-Solution)来解决上面的两个数据问题：
>
> 高水位机制仍然使用、但只用于衡量同步状态，而不再作为数据截断的依据，数据截断交给`Leader Epoch`来判断
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_epoch.jpg)
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
>         ![](https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_leaderepoch_sync_s1.jpg)
>
>       * `Follower`的`start_offset`比`Leader`的返回的`offset`大（左图对应`epoch_id`比新leader的小的情况；右图对应`epoch_id`相等的情况）：Follower根据Leader的数据重置自己的`Leader Epoch文件`并进行日志截断
>
>         ![](https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_leaderepoch_sync_s2.jpg)

用`Leader Epoch机制`解决`高水位机制`的两个问题

> * 解决“Follower重启引发HW截断，同时Leader宕机使Follower（截断尚未恢复）成为Leader时，引发数据丢失（[High Watermark Truncation followed by Immediate Leader Election](https://cwiki.apache.org/confluence/display/KAFKA/KIP-101+-+Alter+Replication+Protocol+to+use+Leader+Epoch+rather+than+High+Watermark+for+Truncation#KIP101AlterReplicationProtocoltouseLeaderEpochratherthanHighWatermarkforTruncation-Scenario1:HighWatermarkTruncationfollowedbyImmediateLeaderElection)）”的问题
>
>   ![](https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_leaderepoch_solve_dataloss.jpg)
>
>   原问题：A重启由于HW还未更新、将m2截断丢弃并连接Leader B同步数据；然而随后B宕机A变成Leader导致消息m2丢失
>
>   引入Epoch后：A重启后并不会根据HW截断m2，而是向B发送Leader Epoch Request，B返回offset 2，B随后宕机发生重新选举使A成为Leader，A生成新的Leader Epoch <lg=2; offset=2>接收数据。因为在A成为leader之前，A的offset不可能比B高，根据上一小节的内容，A不会发生截断，因此也就没机会丢失消息m
>
> * 解决“Follower与Leader同时重启，先启动的Follower（截断尚未恢复）接收新数据时，引发数据不一致的问题（[Replica Divergence on Restart after Multiple Hard Failures](https://cwiki.apache.org/confluence/display/KAFKA/KIP-101+-+Alter+Replication+Protocol+to+use+Leader+Epoch+rather+than+High+Watermark+for+Truncation#KIP101AlterReplicationProtocoltouseLeaderEpochratherthanHighWatermarkforTruncation-Scenario2:ReplicaDivergenceonRestartafterMultipleHardFailures)）”的问题
>
>   ![](https://raw.githubusercontent.com/kenfang119/pics/main/300_kafka/kafka_leaderepoch_solve_inconsist.jpg)
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
>         record,
>         new Callback() {
>             public void onCompletion(RecordMetadata metadata, Exception e) {
>                 if(e != null) {
>                     e.printStackTrace();
>                 } else {
>                     System.out.println("The offset of the record we just sent is: " + metadata.offset());
>                 }
>             }
>         });
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

