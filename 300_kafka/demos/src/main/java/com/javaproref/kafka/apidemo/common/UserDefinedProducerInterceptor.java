package com.javaproref.kafka.apidemo.common;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

public class UserDefinedProducerInterceptor implements ProducerInterceptor {
    @Override
    public ProducerRecord onSend(ProducerRecord record) {
        // 消息发送之前，可以拿到ProducerRecord以便装饰器进行编辑和修改
        // 包括key，value，headers等各种字段
        return new ProducerRecord(record.topic(), record.key(), record.value() + ":content_added_by_interceptor");
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        // 每当消息发送成功、或失败，得到通知时被调用
        // 其中的exception，只有在开启重试策略或应答机制（高级API部分涉及）时才能拿到
        System.out.println("metadata: " + metadata + "; exception: " + exception);
    }

    @Override
    public void close() {
        // 生产者关闭时被调用
    }

    @Override
    public void configure(Map<String, ?> configs) {
        // 能够得到生产者的配置
    }
}
