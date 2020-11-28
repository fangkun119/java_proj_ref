package com.javaproref.kafka.apidemo.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.internals.Topic;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

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
        consumer.seek(new TopicPartition("topic01", 0), 1); // 从topic01分区0的offset为1的消息（即第2条消息）开始消费

        // 3. 打印取到的records
        System.out.println("Type in: Ctrl + C to quit");
        Thread.sleep(3000);
        while (true) {
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(1) /*每隔1秒取一次数据*/);
            if (!consumerRecords.isEmpty()) {
                Iterator<ConsumerRecord<String,String>> recordIterator = consumerRecords.iterator();
                while(recordIterator.hasNext()) {
                    ConsumerRecord<String,String> record = recordIterator.next();
                    String topic    = record.topic();       // 所属topic
                    int partition   = record.partition();   // 所属分区
                    long offset     = record.offset();      // 消息在分区中的偏移量
                    String key      = record.key();         // 消息的key
                    String value    = record.value();       // 消息的value
                    long timestamp  = record.timestamp();   // 消息的时间戳
                    System.out.println(topic + "\t" + partition + "," + offset + "\t"
                            + key + "\t" + value + "\t" + timestamp);
                }
            }
        }

        // 3. 关闭消费者
        // consumer.close();
    }
}
