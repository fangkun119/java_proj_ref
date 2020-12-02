package com.javaproref.kafka.apidemo.consumer;

import com.javaproref.kafka.apidemo.common.ConsumerCommon;
import com.javaproref.kafka.apidemo.util.Constants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class KafkaConsumerPartitionAssignDemo {
    public void runDemo(String bootStrapServers) throws InterruptedException {
        // 1. 创建Consumer
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // props.put(ConsumerConfig.GROUP_ID_CONFIG, "g1"); //不设置消费组
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);

        // 2. 手动指定消费者消费的分区，将失去组管理的特性，同时因为不属于任何组，因此这些消费者之间彼此独立、没有任何关系
        // 指定消费topic01的第0个分区
        List<TopicPartition> partitions = Arrays.asList(new TopicPartition("topic01", 0));
        consumer.assign(partitions);
        // 指定消费的起始位置
        // consumer.seekToBeginning(partitions); // 从消费分区的启示位置开始消费
        consumer.seek(new TopicPartition(Constants.TOPIC_01, 0), 1); // 从topic01分区0的offset为1的消息（即第2条消息）开始消费

        // 3. 打印取到的records
        try {
            ConsumerCommon.consume(consumer, ConsumerCommon.OffsetAutoSubmit.ENABLE);
        } catch (InterruptedException e) {
            throw e;
        } finally {
            consumer.close();
        }
    }
}
