package com.javaprojref.springbootkafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.messaging.handler.annotation.SendTo;

import java.io.IOException;

@SpringBootApplication
public class SpringbootkafkaApplication {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(SpringbootkafkaApplication.class, args);
        System.out.println("intput anything to quit: ");
        System.in.read();
    }

    // 消费者
    @KafkaListeners(value = {
        @KafkaListener(topics = {"topic01"})
    })
    public void receive01(ConsumerRecord<String, String> record) {
        System.out.println("record: " + record);
    }

    // 既是消费者又是生产者
    @KafkaListeners(value = {
        @KafkaListener(topics = {"topic02"})
    })
    @SendTo("topic03")
    public String receive02(ConsumerRecord<String, String> record) {
        return record.value() + "\tforwarded to topic03";
    }
}
