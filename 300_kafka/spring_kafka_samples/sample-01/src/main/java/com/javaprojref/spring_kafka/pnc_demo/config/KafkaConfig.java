package com.javaprojref.spring_kafka.pnc_demo.config;

import com.javaprojref.spring_kafka.pnc_demo.Application;
import com.javaprojref.spring_kafka.pnc_demo.common.Foo2;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    private final Logger logger = LoggerFactory.getLogger(Application.class);
    private final TaskExecutor exec = new SimpleAsyncTaskExecutor();

    // BootStrapServer地址，从配置文件application.yml注入
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    // KafkaAdmin：用于将Topic注入到Application Context中
    // 参考：https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#configuring-topics
    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic01() {
        return TopicBuilder.name("topic01")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topic02() {
        return new NewTopic("topic02", 1, (short) 1);
    }

    // 使用KafkaListener接收数据，参考
    // https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#kafka-listener-annotation
    @KafkaListener(id = "fooListener", topics = "topic01")
    public void topic01Listener(Foo2 foo) {
        logger.info("recieve message from topic01: " + foo);
        if (foo.getFoo().startsWith("fail")) {
            throw new RuntimeException("failed"); //触发异常，交给errorHandler来处理
        }
        this.exec.execute(() -> System.out.println("Hit Enter to terminate..."));
    }

    @KafkaListener(id = "id02", topics = "topic01.DLT", groupId = "failMsgCollectGroup", properties = {
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG + "=latest"
    })
    public void topic01DLTListener(String in) {
        logger.info("receive message from topic01.DLT: " + in);
        this.exec.execute(() -> System.out.println("Hit Enter to terminate..."));
    }

    @Bean
    public RecordMessageConverter converter() {
        return new StringJsonMessageConverter();
    }

    // 使用KafkaTemplate来发送数据，
    // 也可以不定义让框架来提供一个默认的template（相当于注释掉下面三个Bean）
    // 可以在自定义全局的KafkaTemplate Bean，参考：https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#kafka-template
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // See https://kafka.apache.org/documentation/#producerconfigs for more properties
        return props;
    }

    @Bean
    public ProducerFactory<Object, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate() {
        return new KafkaTemplate<Object, Object>(producerFactory());
    }

    // 错误处理，重试失败的record以及之后发送的record，参考：
    // 1. https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#seek-to-current
    // 2. https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#dead-letters
    @Bean
    public SeekToCurrentErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        return new SeekToCurrentErrorHandler(
                new DeadLetterPublishingRecoverer(template),  // 用传入的template将失败的消息转发到名为record.topic() + ".DLT"的topic中
                new FixedBackOff(1000L, 2) // 最多重试2次、重试间隔1000毫秒
        );
    }
}
