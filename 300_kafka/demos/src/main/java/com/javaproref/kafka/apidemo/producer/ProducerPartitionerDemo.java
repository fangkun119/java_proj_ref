package com.javaproref.kafka.apidemo.producer;

import com.javaproref.kafka.apidemo.common.RecordKeyPolicy;
import com.javaproref.kafka.apidemo.common.UserDefinedPartitioner;
import com.javaproref.kafka.apidemo.domain.Constants;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerPartitionerDemo {
    public void runDemo(String bootstrapServers, RecordKeyPolicy recKeyPolicy) {
        // 1. 创建Producer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // 设置自定义分区类
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, UserDefinedPartitioner.class);
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);

         // 2. 发送消息
        for (int i = 0; i < 30; ++i) {
            ProducerRecord<String, String> record = null;
            if (RecordKeyPolicy.ENABLE == recKeyPolicy) {
                record = new ProducerRecord<String, String>(Constants.TOPIC_01, "key_" + i, "value_" + i); //还可以设定分区、时间戳等
            } else {
                record = new ProducerRecord<String, String>(Constants.TOPIC_01, "value_" + i);
            }
            producer.send(record);
        }

        // 3. 关闭生产者
        producer.close();
    }
}
