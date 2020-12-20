package com.javaprojref.springbootkafka.service;

public interface IMessageSender {
    public void sendMessage(String topic, String key, String message);
}
