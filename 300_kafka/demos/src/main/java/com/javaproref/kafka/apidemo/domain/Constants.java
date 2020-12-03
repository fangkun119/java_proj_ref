package com.javaproref.kafka.apidemo.domain;

public class Constants {
    public static final String TOPIC_01 = "topic01";  // 消息类型： <string, string>
    public static final String TOPIC_02 = "topic02";  // 消息类型： <string, User>
    // 实验用的topic都加在数组中，共统一创建和销毁
    public static final String[] DEMO_TOPICS = {TOPIC_01, TOPIC_02};
}
