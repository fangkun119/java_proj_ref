package com.javaprojref.spring_kafka.transaction_demo.config;

import com.javaprojref.spring_kafka.transaction_demo.Application;
import com.javaprojref.spring_kafka.transaction_demo.domain.consumer.Foo2;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Configuration
public class KafkaConfig {
    // 辅助对象
    public final static CountDownLatch APP_CLOSE_LATCH = new CountDownLatch(1);
    private final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // 调用KafkaAdmin创建Topic并将Topic注入到Application Context中
    // 参考：https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#configuring-topics
    @Bean
    public NewTopic topic02() {
        return TopicBuilder.name("topic02").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic topic03() {
        return TopicBuilder.name("topic03").partitions(1).replicas(1).build();
    }

    // 使用KafkaListener接收数据，参考
    // https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#kafka-listener-annotation
    @KafkaListener(id = "topic02Listener", topics = "topic02")
    public void listen02(List<Foo2> foos) throws IOException {
        LOGGER.info("Received: " + foos);
        foos.forEach(f -> kafkaTemplate.send("topic03", f.getFoo().toUpperCase()));
        LOGGER.info("Messages sent, hit Enter to commit tx");
        System.in.read();
    }

    @KafkaListener(id = "topic03listener", topics = "topic03")
    public void listen03(List<String> in) {
        LOGGER.info("Received: " + in);
        APP_CLOSE_LATCH.countDown(); //触发主线程退出
    }

    // Consumer的反序列化配置，参考
    // 1. https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#serdes
    // 2. https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#payload-conversion-with-batch
    @Bean
    public RecordMessageConverter converter() {
        return new StringJsonMessageConverter();
    }

    @Bean
    public BatchMessagingMessageConverter batchConverter() {
        return new BatchMessagingMessageConverter(converter());
    }
}
