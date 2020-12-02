package com.javaproref.kafka.apidemo;

import com.javaproref.kafka.apidemo.consumer.KafkaConsumerPartitionAssignDemo;
import com.javaproref.kafka.apidemo.consumer.KafkaConsumerSubscribeDemo;
import com.javaproref.kafka.apidemo.dml.CreateTestingTopic;
import com.javaproref.kafka.apidemo.dml.KafkaTopicDMLDemo;
import com.javaproref.kafka.apidemo.dml.RemoveTestingTopic;
import com.javaproref.kafka.apidemo.offsets.KafkaOffsetResetDemo;
import com.javaproref.kafka.apidemo.producer.KafkaProducerDemo;

import java.util.concurrent.ExecutionException;

public class Main {
    public static final String BOOT_STRAP_SVRS = "CentOSA:9092,CentOSB:9092,CentOSC:9092";
    public static final String TOPIC_DML = "topic_dml";
    public static final String PRODUCER  = "producer";
    public static final String CONSUMER  = "consumer";
    public static final String ASSIGN_CONSUMER_PARTITION = "assign_consumer_partition";
    public static final String CLEAR     = "clear";
    public static final String INIT      = "init";
    public static final String OFFSET_LATEST = "offset_latest";
    public static final String OFFSET_EARLIEST = "offset_earliest";
    public static final String OFFSET_NONE = "offset_none";

    private static void printHelp(String[] args) {
        System.out.println(
                "usage: "
                        + "\n\t" + "java -jar ${jar_file_path} ${demo_name} ${bootstrap_servers}\n\n" +
                        "bootstrap_servers: "
                        + "\n\t optional parameter, default value is : CentOSA:9092,CentOSB:9092,CentOSC:9092" +
                        "demo_name: "
                        + "\n\t" + Main.TOPIC_DML
                        + "\n\t" + Main.CLEAR
                        + "\n\t" + Main.INIT
                        + "\n\t" + Main.PRODUCER
                        + "\n\t" + Main.CONSUMER
                        + "\n\t" + Main.ASSIGN_CONSUMER_PARTITION
                        + "\n\t" + Main.OFFSET_LATEST
                        + "\n\t" + Main.OFFSET_EARLIEST
                        + "\n\t" + Main.OFFSET_NONE
        );
    }

    public static void main(String[] args) {
        // Stream.of(args).forEach(System.out::println);
        if (null == args || args.length < 1) {
            Main.printHelp(args);
            return;
        }

        // boot strap servers
        String bootStrapSvrs = (args.length >= 2) ? args[1] : BOOT_STRAP_SVRS;

        // run demo
        try {
            switch (args[0]) {
                case Main.TOPIC_DML:
                    (new KafkaTopicDMLDemo()).runDemo(bootStrapSvrs);
                    break;
                case Main.CLEAR:
                    (new RemoveTestingTopic()).runDemo(bootStrapSvrs);
                    break;
                case Main.INIT:
                    (new CreateTestingTopic()).runDemo(bootStrapSvrs);
                case Main.PRODUCER:
                    (new KafkaProducerDemo()).runDemo(bootStrapSvrs);
                    break;
                case Main.CONSUMER:
                    (new KafkaConsumerSubscribeDemo()).runDemo(bootStrapSvrs);
                    break;
                case Main.ASSIGN_CONSUMER_PARTITION:
                    (new KafkaConsumerPartitionAssignDemo()).runDemo(bootStrapSvrs);
                    break;
                case Main.OFFSET_LATEST:
                    (new KafkaOffsetResetDemo()).runDemo(bootStrapSvrs, KafkaOffsetResetDemo.LATEST);
                case Main.OFFSET_EARLIEST:
                    (new KafkaOffsetResetDemo()).runDemo(bootStrapSvrs, KafkaOffsetResetDemo.EARLIEST);
                case Main.OFFSET_NONE:
                    (new KafkaOffsetResetDemo()).runDemo(bootStrapSvrs, KafkaOffsetResetDemo.NONE);
                default:
                    Main.printHelp(args);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
