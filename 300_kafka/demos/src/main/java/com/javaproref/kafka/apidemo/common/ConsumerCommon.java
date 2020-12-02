package com.javaproref.kafka.apidemo.common;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConsumerCommon {
    public enum OffsetAutoSubmit {
        ENABLE, DISABLE
    };

    public static void consume(KafkaConsumer<String, String> consumer, OffsetAutoSubmit autoSubmitOffset) throws InterruptedException {
        System.out.println("Type in: Ctrl + C to quit");
        Thread.sleep(3000);

        while (true) {
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(1) /*每隔1秒取一次数据*/);
            if (!consumerRecords.isEmpty()) {
                // iterator and offset map
                Iterator<ConsumerRecord<String,String>> recordIterator = consumerRecords.iterator();
                Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
                // consume records
                while(recordIterator.hasNext()) {
                    // record
                    ConsumerRecord<String,String> record = recordIterator.next();
                    // 常用字段
                    String topic    = record.topic();       // 所属topic
                    int partition   = record.partition();   // 所属分区
                    long offset     = record.offset();      // 消息在分区中的偏移量
                    String key      = record.key();         // 消息的key
                    String value    = record.value();       // 消息的value
                    long timestamp  = record.timestamp();   // 消息的时间戳
                    // 手动提交offset
                    if (OffsetAutoSubmit.DISABLE == autoSubmitOffset) {
                        offsets.put(new TopicPartition(topic, partition), new OffsetAndMetadata(offset));
                        consumer.commitAsync(offsets, new OffsetCommitCallback() {
                            @Override
                            public void onComplete(Map<TopicPartition, OffsetAndMetadata> map, Exception e) {
                                System.out.println("offsets: " + offset + "\t exception: " + e);
                            }
                        });
                    }
                    // 输出
                    System.out.println(topic + "\t" + partition + "," + offset + "\t"
                            + key + "\t" + value + "\t" + timestamp);
                }
            }
        }
    }
}
