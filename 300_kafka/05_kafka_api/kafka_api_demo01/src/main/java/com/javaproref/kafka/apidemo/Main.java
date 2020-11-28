package com.javaproref.kafka.apidemo;

import com.javaproref.kafka.apidemo.consumer.KafkaConsumerDemo;
import com.javaproref.kafka.apidemo.dml.KafkaTopicDMLMemo;
import com.javaproref.kafka.apidemo.producer.KafkaProducerDemo;

import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class Main {
    public static final String BOOT_STRAP_SVRS = "CentOSA:9092,CentOSB:9092,CentOSC:9092";
    public static final String TOPIC_DML = "topic_dml";
    public static final String PRODUCER  = "producer";
    public static final String CONSUMER  = "consumer";

    public static void main(String[] args) {
        // Stream.of(args).forEach(System.out::println);
        if (null == args || args.length < 1) {
            Main.printHelp(args);
        }

        // boot strap servers
        String bootStrapSvrs = (args.length >= 2) ? args[1] : BOOT_STRAP_SVRS;

        // run demo
        try {
            switch (args[0]) {
                case Main.TOPIC_DML:
                    (new KafkaTopicDMLMemo()).runDemo(bootStrapSvrs);
                    break;
                case Main.PRODUCER:
                    (new KafkaProducerDemo()).runDemo(bootStrapSvrs);
                    break;
                case Main.CONSUMER:
                    (new KafkaConsumerDemo()).runDemo(bootStrapSvrs);
                    break;
                default:
                    Main.printHelp(args);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void printHelp(String[] args) {
        System.out.println(
            "usage: "
            + "\n\t" + "java -jar ${jar_file_path} ${demo_name} ${bootstrap_servers}\n\n" +
            "bootstrap_servers: "
            + "\n\t optional parameter, default value is : CentOSA:9092,CentOSB:9092,CentOSC:9092" +
            "demo_name: "
            + "\n\t" + Main.TOPIC_DML
            + "\n\t" + Main.PRODUCER
            + "\n\t" + Main.CONSUMER
        );
    }
}
