package com.javaproref.kafka.apidemo.offsets;

import com.javaproref.kafka.apidemo.common.ConsumerCommon;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.Properties;

public class AutoOffsetResetConfigDemo {
    public final static String LATEST = "latest";
    public final static String EARLIEST = "earliest";
    public final static String NONE = "none";

    public void runDemo(String bootStrapServers, String autoOffsetResetConfig) throws InterruptedException {
        // 1. Consumer配置
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "g1");
        // 消费者offset起始位置（对于新加入的消费者组）：
        //    latest：找不到先前消偏移量时、从最新记录开始消费
        //    earliest：找不到先前偏移量时，从最早记录开始消费
        //    none：如果找不到消费者组先前的偏移量时，向消费者抛出异常
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetResetConfig);
        // 是否开启offset自动提交
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        // offset自动提交间隔
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 5000);

        // 2. 创建消费者
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);

        // 3. 订阅"topic01"
        consumer.subscribe(Arrays.asList("topic01"));

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

