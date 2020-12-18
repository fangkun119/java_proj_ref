package com.javaproref.kafka.apidemo.common;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import javax.xml.bind.ValidationEvent;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConsumerCommon {
    public enum OffsetAutoSubmit {
        TRUE, FALSE
    };

    public static <KeyType, ValType> void consume(
            KafkaConsumer<KeyType, ValType> consumer, OffsetAutoSubmit autoSubmitOffset) throws InterruptedException {
        System.out.println("Type in: Ctrl + C to quit");
        Thread.sleep(3000);

        while (true) {
            ConsumerRecords<KeyType, ValType> consumerRecords = consumer.poll(Duration.ofSeconds(1)); //每隔1秒取一次数据
            if (!consumerRecords.isEmpty()) {
                // iterator and offset map
                Iterator<ConsumerRecord<KeyType,ValType>> recordIterator = consumerRecords.iterator();
                Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
                // consume records
                while(recordIterator.hasNext()) {
                    // record
                    ConsumerRecord<KeyType,ValType> record = recordIterator.next();
                    // 常用字段
                    String topic    = record.topic();       // 所属topic
                    int partition   = record.partition();   // 所属分区
                    long offset     = record.offset();      // 消息在分区中的偏移量
                    KeyType key     = record.key();         // 消息的key
                    ValType value   = record.value();       // 消息的value
                    long timestamp  = record.timestamp();   // 消息的时间戳
                    // 手动提交offset
                    if (OffsetAutoSubmit.FALSE == autoSubmitOffset) {
                        long submittedOffset = offset + 1; // 提交的偏移量要大于当前消费的偏移量，因为它代表下一条消费信息的偏移量
                        offsets.put(
                                new TopicPartition(topic, partition),
                                new OffsetAndMetadata(submittedOffset)
                        );
                        consumer.commitAsync(offsets, new OffsetCommitCallback() {
                            @Override
                            public void onComplete(Map<TopicPartition, OffsetAndMetadata> map, Exception e) {
                                System.out.println("offsets: " + submittedOffset + "\t exception: " + e);
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
