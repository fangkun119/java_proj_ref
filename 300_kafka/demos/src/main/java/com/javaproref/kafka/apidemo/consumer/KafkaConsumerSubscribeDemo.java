package com.javaproref.kafka.apidemo.consumer;

import com.javaproref.kafka.apidemo.common.ConsumerCommon;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

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

        // 2. 订阅所有名称以"topic"开头的Topics
        consumer.subscribe(Pattern.compile("^topic.*"));

        // 3. 接收消息
        try {
            ConsumerCommon.consume(consumer, ConsumerCommon.OffsetAutoSubmit.TRUE);
        } catch (InterruptedException e) {
            throw e;
        } finally {
            consumer.close();
        }
    }
}
