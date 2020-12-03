package com.javaproref.kafka.apidemo.offsets;

import com.javaproref.kafka.apidemo.common.ConsumerCommon;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.Properties;

public class OffsetConsumerSubmitDemo {
    public final static String LATEST = "latest";
    public final static String EARLIEST = "earliest";
    public final static String NONE = "none";

    public void runDemo(String bootStrapServers) throws InterruptedException {
        // 1. Consumer配置
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "g1");
        // 新加入的消费组从topic第一条记录开始消费，方便测试
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        // 关闭offset自动提交
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // 2. 创建消费者
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);

        // 3. 订阅"topic01"
        consumer.subscribe(Arrays.asList("topic01"));

        // 3. 接收消息，以手动方式提交offset："autoSubmitOffset == DISABLE"
        try {
            ConsumerCommon.consume(consumer, ConsumerCommon.OffsetAutoSubmit.FALSE);
        } catch (InterruptedException e) {
            throw e;
        } finally {
            consumer.close();
        }
    }
}

