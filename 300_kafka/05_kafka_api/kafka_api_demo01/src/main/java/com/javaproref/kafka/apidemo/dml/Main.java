package com.javaproref.kafka.apidemo.dml;

import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {
        try {
            // run demos
            KafkaTopicDML.runDemo();


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
