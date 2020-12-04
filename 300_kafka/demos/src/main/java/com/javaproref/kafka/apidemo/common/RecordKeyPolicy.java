package com.javaproref.kafka.apidemo.common;

/**
 * Producer在发送消息时，是否生成Record Key，会影响到Partitioner的分区策略
 */
public enum RecordKeyPolicy {
    ENABLE,   // 生成 Record Key
    DISABLE   // 不生成 Record Key
}
