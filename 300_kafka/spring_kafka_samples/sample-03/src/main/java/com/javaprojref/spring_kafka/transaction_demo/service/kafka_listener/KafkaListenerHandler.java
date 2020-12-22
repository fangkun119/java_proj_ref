package com.javaprojref.spring_kafka.transaction_demo.service.kafka_listener;

import com.javaprojref.spring_kafka.transaction_demo.Application;
import com.javaprojref.spring_kafka.transaction_demo.domain.consumer.Foo2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class KafkaListenerHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void handleTopic02(List<Foo2> foos) throws IOException {
        LOGGER.info("Received: " + foos);
        foos.forEach(f -> kafkaTemplate.send("topic03", f.getFoo().toUpperCase()));
        LOGGER.info("Messages forwarded to topic03, hit Enter to continue");
        System.in.read();
    }

    public void handleTopic03(List<String> strList) {
        LOGGER.info("Received: " + strList);
    }
}
