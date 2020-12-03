package com.javaproref.kafka.apidemo.consumer;

import com.javaproref.kafka.apidemo.common.ConsumerCommon;
import com.javaproref.kafka.apidemo.common.UserDefinedDeSerializer;
import com.javaproref.kafka.apidemo.domain.Constants;
import com.javaproref.kafka.apidemo.domain.User;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.Properties;

public class ConsumerDeserializationDemo {
    public void runDemo(String bootStrapServers) throws InterruptedException {
        // 1. 创建Consumer
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "g2");
        // 指定consumer group新加入时，从第一条消息开始消费，方便实验（默认是"latest")
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Value类型不再是String，而是自定义的User类
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserDefinedDeSerializer.class.getName());
        KafkaConsumer<String, User> consumer = new KafkaConsumer<>(props);

        // 2. 订阅TOPIC02
        consumer.subscribe(Arrays.asList(Constants.TOPIC_02));

        // 3. 接收消息
        try {
            ConsumerCommon.<String, User>consume(consumer, ConsumerCommon.OffsetAutoSubmit.TRUE);
        } catch (InterruptedException e) {
            throw e;
        } finally {
            consumer.close();
        }
    }
}
