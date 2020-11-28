package com.javaproref.kafka.apidemo.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Pattern;

public class KafkaConsumerSubscribeDemo {
    public void runDemo(String bootStrapServers) throws InterruptedException {
        // 1. 创建Consumer
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "g1");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);

        // 2. 订阅Topics
        consumer.subscribe(Pattern.compile("^topic.*")); //订阅所有名称以"topic"开头的Topics

        // 3. 遍历消息队列
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
