package com.javaproref.kafka.apidemo;

import com.javaproref.kafka.apidemo.consumer.KafkaConsumerPartitionAssignDemo;
import com.javaproref.kafka.apidemo.consumer.KafkaConsumerSubscribeDemo;
import com.javaproref.kafka.apidemo.dml.CreateTestingTopic;
import com.javaproref.kafka.apidemo.dml.KafkaTopicDMLDemo;
import com.javaproref.kafka.apidemo.dml.RemoveTestingTopic;
import com.javaproref.kafka.apidemo.offsets.AutoOffsetResetConfigDemo;
import com.javaproref.kafka.apidemo.offsets.OffsetConsumerSubmitDemo;
import com.javaproref.kafka.apidemo.producer.KafkaProducerDemo;

import java.util.concurrent.ExecutionException;

public class Main {
    // bootstrap server 默认值，非默认值可通过第二个参数传入
    public static final String BOOT_STRAP_SVRS = "CentOSA:9092,CentOSB:9092,CentOSC:9092";

    // demo主题，通过第一个参数传入
    public static final String CLEAR            = "clear";              // 清理实验环境
    public static final String INIT             = "init";               // 初始化实验环境
    public static final String TOPIC_DML        = "topic_dml";          // topic管理演示
    public static final String PRODUCER         = "producer";           // 生产者演示
    public static final String CONSUMER         = "consumer";           // 消费者演示
    public static final String ASSIGN_CONSUMER_PARTITION = "assign_consumer_partition"; // 手动指定消费者消费的分区
    public static final String OFFSET_LATEST    = "offset_latest";      // 指定新加入消费组起始偏移量为"latest"
    public static final String OFFSET_EARLIEST  = "offset_earliest";    // 指定新加入消费组起始偏移量为"earliest"
    public static final String OFFSET_NONE      = "offset_none";        // 指定新加入消费组起始偏移量为"none"
    public static final String OFFSET_CONSUMER_SUBMIT = "offset_consumer_submit";  // 由消费者代码调用api提交offset，而非由框架定期自动提交

    // 打印帮助信息
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
                        + "\n\t" + Main.OFFSET_CONSUMER_SUBMIT
        );
    }

    // 主函数
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
                case Main.OFFSET_EARLIEST:
                    (new AutoOffsetResetConfigDemo()).runDemo(bootStrapSvrs, AutoOffsetResetConfigDemo.EARLIEST);
                    break;
                case Main.OFFSET_LATEST:
                    (new AutoOffsetResetConfigDemo()).runDemo(bootStrapSvrs, AutoOffsetResetConfigDemo.LATEST);
                    break;
                case Main.OFFSET_NONE:
                    (new AutoOffsetResetConfigDemo()).runDemo(bootStrapSvrs, AutoOffsetResetConfigDemo.NONE);
                    break;
                case Main.OFFSET_CONSUMER_SUBMIT:
                    (new OffsetConsumerSubmitDemo()).runDemo(bootStrapSvrs);
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
