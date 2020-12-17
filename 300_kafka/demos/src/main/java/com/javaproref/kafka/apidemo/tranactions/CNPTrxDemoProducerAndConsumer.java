package com.javaproref.kafka.apidemo.tranactions;

import com.javaproref.kafka.apidemo.common.ConsumerCommon;
import com.javaproref.kafka.apidemo.domain.Constants;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.*;

public class CNPTrxDemoProducerAndConsumer {
    // 运行Demo
    public void runDemo(String bootStrapServers) {
        // 1. 这个节点既是消费者又是生产者
        String consumerGroupId = "g1";
        KafkaConsumer<String, String> consumer = buildKafkaConsumer(bootStrapServers, consumerGroupId);
        KafkaProducer<String, String> producer = buildKafkaProducer(bootStrapServers);

        // 2. 消费者订阅TOPIC01
        consumer.subscribe(Arrays.asList(Constants.TOPIC_01));

        // 3. 处理消息
        while (true) {
            // 从Kafka队列取上游数据
            ConsumerRecords<String,String> consumerRecords = consumer.poll(Duration.ofSeconds(1));
            if (false == consumerRecords.isEmpty()) {
                Map<TopicPartition,OffsetAndMetadata> offsets = new HashMap<>();
                Iterator<ConsumerRecord<String,String>> recordIterator = consumerRecords.iterator();
                // (1) 开始事务
                producer.beginTransaction();
                try {
                    // (2) 迭代数据、开始业务处理
                    while (recordIterator.hasNext()) {
                        // 从上游topic01发来的record
                        ConsumerRecord<String,String> record = recordIterator.next();
                        // 存储元数据
                        offsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
                        // 发往下游topic02
                        ProducerRecord<String,String> recordToForward = new ProducerRecord<>(
                                Constants.TOPIC_02, record.key(), record.value() + " forwarded");
                        producer.send(recordToForward);
                    }
                    // (3A) 提交事务
                    producer.sendOffsetsToTransaction(offsets, consumerGroupId);
                    producer.commitTransaction();
                } catch (Exception e) {
                    // (3B) 终止事务
                    System.err.println("exception: " + e.getMessage());
                    producer.abortTransaction();
                }
            }
        }
    }

    public KafkaConsumer<String, String> buildKafkaConsumer(String bootStrapServers, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // 设置消费者的消费事务的隔离级别为read_committed来忽略来自失败事务的数据
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        // 对于消费者&生产者事务中既是生产者、又是消费者的节点
        // 必须关闭消费者端offset的自动提交，待下游业务确认完成后，主动提交offset
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new KafkaConsumer<>(props);
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