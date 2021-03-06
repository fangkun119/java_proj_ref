package com.javaproref.kafka.apidemo.acks;

import com.javaproref.kafka.apidemo.common.ConsumerCommon;
import com.javaproref.kafka.apidemo.common.UserDefinedDeSerializer;
import com.javaproref.kafka.apidemo.domain.Constants;
import com.javaproref.kafka.apidemo.domain.User;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.Properties;

public class AckDemoConsumer {
    public void runDemo(String bootStrapServers) throws InterruptedException {
        // 1. 创建Consumer
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "g1");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // 2. 订阅TOPIC01
        consumer.subscribe(Arrays.asList(Constants.TOPIC_01));

        // 3. 接收消息
        try {
            ConsumerCommon.<String, String>consume(consumer, ConsumerCommon.OffsetAutoSubmit.TRUE);
        } catch (InterruptedException e) {
            throw e;
        } finally {
            consumer.close();
        }
    }
}
