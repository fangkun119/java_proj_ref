package com.javaprojref.spring_kafka.multi_method_demo.config;

import com.javaprojref.spring_kafka.multi_method_demo.domain.Bar2;
import com.javaprojref.spring_kafka.multi_method_demo.domain.Foo2;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.converter.Jackson2JavaTypeMapper;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    // 调用KafkaAdmin创建Topic并将Topic注入到Application Context中
    // 参考：https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#configuring-topics
    @Bean
    public NewTopic topicFoos() {
        return new NewTopic("foos", 1, (short) 1);
    }

    @Bean
    public NewTopic topicBars() {
        return new NewTopic("bars", 1, (short) 1);
    }

    // 错误处理，重试失败的record以及之后发送的record，参考：
    // 1. https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#seek-to-current
    // 2. https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#dead-letters
    @Bean
    public SeekToCurrentErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        return new SeekToCurrentErrorHandler(
                new DeadLetterPublishingRecoverer(template),   // 用传入的template将失败的消息转发到名为record.topic() + ".DLT"的topic中
                new FixedBackOff(1000L, 2)  // 最多重试2次、重试间隔1000毫秒
        );
    }

    // 消费者在反序列化时的类型映射配置，参考
    // https://www.confluent.io/blog/spring-for-apache-kafka-deep-dive-part-1-error-handling-message-conversion-transaction-support/
    @Bean
    public RecordMessageConverter converter() {
        // 生产者序列化时，如何设置类型TYPE_ID?
        //    在application.yml的spring.kafka.producer.properties.spring.json.type.mapping配置中可以看到
        //    foo:com.javaprojref.spring_kafka.multi_method_demo.domain.Foo1,bar:com.javaprojref.spring_kafka.multi_method_demo.domain.Bar1
        //    它把Foo1类型数据的TYPE_ID设为foo，Bar1类型数据的TYPE_ID设为bar：

        // 消费者反序列化时如何根据TYPE_ID查找对应的类型？
        //    在下面的map中可以看到：foo反序列化为Foo2，bar反序列化为Bar2
        Map<String, Class<?>> mappings = new HashMap<>();
        mappings.put("foo", Foo2.class);
        mappings.put("bar", Bar2.class);

        // 反序列化时该解析为哪个类：TYPE_ID（根据message header）；INFERRED（根据方法参数推断）
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        typeMapper.addTrustedPackages("com.javaprojref.spring_kafka.multi_method_demo.domain"); //解序列化时所信任的package
        typeMapper.setIdClassMapping(mappings);

        // 使用上面Map中定义的映射关系
        StringJsonMessageConverter converter = new StringJsonMessageConverter();
        converter.setTypeMapper(typeMapper);
        return converter;
    }
}

