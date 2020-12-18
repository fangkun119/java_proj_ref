# Kafka进阶

## 1 高水位（High Water Mark)

背景：0.11版本之前的Kafka使用高水位机制来维护Leader和Follower之间的数据同步状态

> * `Topic`被分为多个`分区`，`分区`按照`Segments`存储文件块，这样可以根据偏移量快速查找分区块，Kafka可以保证分区里的事件是有序的，`Leader`负责对分区的读写、`Follower`负责同步分区的数据
> * 高水位机制被用于`0.11`版本之前Kafka的`Leader`和`Followers`建的数据同步（低于`HW`的`Record`表示已经同步给了所有`Followers`，而高于`HW`则不保证）

相关概念：

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
>   * `Leader`变成`Follower`时，向新选举的`Leader`发送`Leader Epoch Request`，请求包含当前（已变成follower）节点所存最新的`<epoch_id, start_offset>`，而新`Leader`则返回`Leader`自身最`新epoch_id`的`<epoch_id, start_offset或LEO>`
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

2.  

