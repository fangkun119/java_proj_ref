package com.javaprojref.springbootkafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = SpringbootkafkaApplication.class)
@RunWith(SpringRunner.class)
class SpringbootkafkaApplicationTests {
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Test
    public void testSendMessage() {
        // 需要关闭application.properties中的事务开关，在application.properties文件中
        // 注释掉 spring.kafka.producer.transaction-id-prefix=transaction-id-
        kafkaTemplate.send(new ProducerRecord<String,String>(
            "topic02", "key001", "value001"
        ));
    }

    @Test
    public void testSendMessageWithTransaction() {
        kafkaTemplate.executeInTransaction(
            new KafkaOperations.OperationsCallback<String, String, Object>() {
                @Override
                public Object doInOperations(KafkaOperations<String, String> kafkaOperations) {
                    kafkaOperations.send(new ProducerRecord<String,String>("topic02", "key002", "value002"));
                    return null;
                }
            }
        );
    }
}
