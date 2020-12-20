package com.javaprojref.springbootkafka;

import com.javaprojref.springbootkafka.service.IMessageSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = SpringbootkafkaApplication.class)
@RunWith(SpringRunner.class)
public class MessageSenderTests {
    @Autowired
    private IMessageSender messageSender;

    @Test
    public void testSendMessage1() {
        messageSender.sendMessage("topic02", "key_003", "value_003 from iMessageSenderTest");
    }
}
