package com.javaproref.kafka.apidemo.producer;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UserDefinedPartitioner implements Partitioner {
    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int partitionNum = partitions.size();
        if (null == keyBytes) {
            // 轮询
            int addIncrement = counter.getAndIncrement();
            return Utils.toPositive(addIncrement) % partitionNum;
        } else {
            // 按key分区
            return Utils.toPositive(Utils.murmur2(keyBytes)) % partitionNum;
        }
    }

    @Override
    public void close() {
        // 实例关闭时被调用
        System.out.println("user defined partitioner closed");
    }

    @Override
    public void configure(Map<String, ?> configs /*生产者配置*/) {
        // 实例创建时被调用
        System.out.println("configs send to user defined partitioner");
        configs.entrySet().stream().forEach(entry -> {
            System.out.println("\t" + entry.getKey() + "\t:" + entry.getValue());
        });
    }
}
