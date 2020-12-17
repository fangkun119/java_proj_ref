package com.javaproref.kafka.apidemo.tranactions;

import com.javaproref.kafka.apidemo.common.ConsumerCommon;
import com.javaproref.kafka.apidemo.domain.Constants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.Properties;

public class CNPTrxDemoConsumer {
    // 运行Demo
    public void runDemo(String bootStrapServers) throws InterruptedException {
        // 1. 创建Consumer
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "g1");
        // 设置消费者的消费事务的隔离级别为read_committed来忽略来自失败事务的数据
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // 2. 订阅TOPIC02 (CNPTrxDemoProducerAndConsumer.java将TOPIC_01发来的数据转发给TOPIC_02)
        consumer.subscribe(Arrays.asList(Constants.TOPIC_02));

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
