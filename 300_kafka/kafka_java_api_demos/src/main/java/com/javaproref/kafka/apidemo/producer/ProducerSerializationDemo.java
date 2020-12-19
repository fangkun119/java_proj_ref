package com.javaproref.kafka.apidemo.producer;

import com.javaproref.kafka.apidemo.common.UserDefinedSerializer;
import com.javaproref.kafka.apidemo.domain.Constants;
import com.javaproref.kafka.apidemo.domain.User;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Date;
import java.util.Properties;

public class ProducerSerializationDemo {
    public void runDemo(String bootstrapServers) {
        // 1. 创建Producer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Value的类型不再是String，而是自定义的类"User"
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UserDefinedSerializer.class.getName());
        KafkaProducer<String, User> producer = new KafkaProducer<>(props);

        // 2. 发送消息
        for (int i = 0; i < 5; ++i) {
            ProducerRecord<String, User> record = null;
            record = new ProducerRecord<String, User>(
                        Constants.TOPIC_02,
                        "key_" + i,
                        new User(i, "user_" + i, new Date()));
            producer.send(record);
        }

        // 3. 关闭生产者
        producer.close();
    }
}
