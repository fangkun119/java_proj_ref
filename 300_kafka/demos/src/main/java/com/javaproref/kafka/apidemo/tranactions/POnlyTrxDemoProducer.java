package com.javaproref.kafka.apidemo.tranactions;

import com.javaproref.kafka.apidemo.domain.Constants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

public class POnlyTrxDemoProducer {
    public void runDemo(String bootstrapServers) {
        // 1. 创建Producer
        KafkaProducer<String, String> producer = buildKafkaProducer(bootstrapServers);

        // 2 用事务发送消息
        // (1) 事务初始化
        producer.initTransactions();
        try {
            // (2) 事务开始
            producer.beginTransaction();
            for (int i = 0; i < 30; ++i) {
                if (8 == i) {
                    // 在发送第8条数据的时候引发一个异常，构造事务终止的场景
                    int j = 10 / 0;
                }
                ProducerRecord<String, String> record = new ProducerRecord<>(Constants.TOPIC_01, 1/*partition*/, "key " + i, "value " + i);
                producer.send(record);
                producer.flush(); // 确保前7条record在异常被触发之前发出
            }
            // (3) 事务提交
            producer.commitTransaction();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            // (4) 事务终止
            producer.abortTransaction();
        }

        // 3. 关闭生产者
        producer.close();
    }

    public KafkaProducer<String, String> buildKafkaProducer(String bootstrapServers) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 使用Kafka事务，必须配置事务ID，并且必须是唯一的
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transaction-id" + UUID.randomUUID().toString());

        // 设置Kafka的重试机制和幂等性
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);  // 开启Kafka的幂等特性,它要求ACKS_CONFIG设为true，同时设置RETRIES_CONFIG
        props.put(ProducerConfig.ACKS_CONFIG, "all");               // 要求Kafka把消息同步到所有副本才返回ACK
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 20000); // 20000毫秒未收到ACK则认为消息超时

        // 为了方便观察，更改批处理配置
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 1024);      // 批处理大小：为了方便观察减小到1024字节
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);          // 批处理等待时长：等待5毫秒，如果没有攒够1024字节数据，也会发送

        return new KafkaProducer<>(props);
    }
}
