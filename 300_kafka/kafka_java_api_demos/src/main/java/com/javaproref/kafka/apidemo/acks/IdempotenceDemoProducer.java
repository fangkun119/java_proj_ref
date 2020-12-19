package com.javaproref.kafka.apidemo.acks;

import com.javaproref.kafka.apidemo.domain.Constants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class IdempotenceDemoProducer {
    public void runDemo(String bootstrapServers) {
        // 1. 创建Producer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 设置Kafka Acks以及Retries
        props.put(ProducerConfig.ACKS_CONFIG, "all");           // 要求Kafka把消息同步到所有副本才返回ACK
        props.put(ProducerConfig.RETRIES_CONFIG, 3);            // 最多重试3次（加上初始发送总共4次）
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 1); // 设置足够小的超时时间，让Producer在收到ACK之间就超时重试
        // 开启Kafka的幂等特性,它要求ACKS_CONFIG设为true，同时设置RETRIES_CONFIG
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        // 允许有多少个未经ACK的请求在同时发送（异步发送），最大值不超过5，值大于1时能增加吞吐量但是会导致下游接收消息时乱序
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // 2. 只发一条消息，让它超时
        ProducerRecord<String, String> record
                = new ProducerRecord<String, String>(Constants.TOPIC_01, "test_ack_key", "test_ack");
        producer.send(record);
        producer.flush(); // 防止producer在本地缓冲这条数据，确保它把消息发出去

        // 3. 关闭生产者
        producer.close();
    }
}
