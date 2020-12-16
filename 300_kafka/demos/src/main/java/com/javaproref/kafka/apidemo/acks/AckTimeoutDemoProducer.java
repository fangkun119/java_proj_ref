package com.javaproref.kafka.apidemo.acks;

import com.javaproref.kafka.apidemo.domain.Constants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class AckTimeoutDemoProducer {
    public void runDemo(String bootstrapServers, String ackCfg) {
        // 1. 创建Producer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 设置Kafka Acks以及Retries
        props.put(ProducerConfig.ACKS_CONFIG, ackCfg);          // ack方式由外部传入
        props.put(ProducerConfig.RETRIES_CONFIG, 3);            // 最多重试3次（加上初始发送总共4次）
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 1); // 设置足够小的超时时间，让Producer在收到ACK之间就超时重试

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
