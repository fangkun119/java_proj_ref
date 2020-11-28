package com.javaproref.kafka.apidemo.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Iterator;

public class ConsumerCommon {
    public static void consume(KafkaConsumer<String, String> consumer) throws InterruptedException {
        System.out.println("Type in: Ctrl + C to quit");
        Thread.sleep(3000);

        while (true) {
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(1) /*每隔1秒取一次数据*/);
            if (!consumerRecords.isEmpty()) {
                Iterator<ConsumerRecord<String,String>> recordIterator = consumerRecords.iterator();
                while(recordIterator.hasNext()) {
                    ConsumerRecord<String,String> record = recordIterator.next();
                    String topic    = record.topic();       // 所属topic
                    int partition   = record.partition();   // 所属分区
                    long offset     = record.offset();      // 消息在分区中的偏移量
                    String key      = record.key();         // 消息的key
                    String value    = record.value();       // 消息的value
                    long timestamp  = record.timestamp();   // 消息的时间戳
                    System.out.println(topic + "\t" + partition + "," + offset + "\t"
                            + key + "\t" + value + "\t" + timestamp);
                }
            }
        }
    }
}
