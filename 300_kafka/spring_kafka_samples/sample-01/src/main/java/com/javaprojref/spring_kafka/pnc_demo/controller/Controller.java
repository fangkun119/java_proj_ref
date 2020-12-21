package com.javaprojref.spring_kafka.pnc_demo.controller;

import com.javaprojref.spring_kafka.pnc_demo.domain.producer.Foo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Gary Russell
 * @since 2.2.1
 */
@RestController
public class Controller {
    // Producer使用KafkaTemplate发送消息，参考：
    // 1. https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#kafka-template
    // 2. https://docs.spring.io/spring-kafka/docs/2.5.10.RELEASE/reference/html/#with-java-configuration
    // 可以让框架提供默认的，也可以自定义一个Bean注入进来（见Application.java)
    @Autowired
    private KafkaTemplate<Object, Object> template;

    @PostMapping(path = "/send/topic01/{message}")
    public void sendToTopic01(@PathVariable String message) {
        this.template.send("topic01", new Foo1(message)); // value类型是Foo1
    }
}
