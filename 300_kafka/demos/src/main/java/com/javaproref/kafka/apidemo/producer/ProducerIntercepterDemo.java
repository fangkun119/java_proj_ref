package com.javaproref.kafka.apidemo.producer;

import com.javaproref.kafka.apidemo.common.UserDefinedProducerInterceptor;
import com.javaproref.kafka.apidemo.domain.Constants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerIntercepterDemo {
    public void runDemo(String bootstrapServers) {
        // 1. 创建Producer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 设置生产者拦截器
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, UserDefinedProducerInterceptor.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // 2. 发送消息
        for (int i = 0; i < 5; ++i) {
            ProducerRecord<String, String> record = null;
            record = new ProducerRecord<String, String>(
                    Constants.TOPIC_01,
                    "key_" + i,
                    "value_" + i);
            producer.send(record);
        }

        // 3. 关闭生产者
        producer.close();
    }
}
