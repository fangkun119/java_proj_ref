package com.javaproref.kafka.apidemo;

import com.javaproref.kafka.apidemo.acks.AckDemoConsumer;
import com.javaproref.kafka.apidemo.acks.AckTimeoutDemoProducer;
import com.javaproref.kafka.apidemo.acks.IdempotenceDemoProducer;
import com.javaproref.kafka.apidemo.common.RecordKeyPolicy;
import com.javaproref.kafka.apidemo.consumer.ConsumerDeserializationDemo;
import com.javaproref.kafka.apidemo.consumer.KafkaConsumerPartitionAssignDemo;
import com.javaproref.kafka.apidemo.consumer.KafkaConsumerSubscribeDemo;
import com.javaproref.kafka.apidemo.dml.CreateTestingTopic;
import com.javaproref.kafka.apidemo.dml.KafkaTopicDMLDemo;
import com.javaproref.kafka.apidemo.dml.RemoveTestingTopic;
import com.javaproref.kafka.apidemo.offsets.AutoOffsetResetConfigDemo;
import com.javaproref.kafka.apidemo.offsets.OffsetConsumerSubmitDemo;
import com.javaproref.kafka.apidemo.producer.KafkaProducerDemo;
import com.javaproref.kafka.apidemo.producer.ProducerIntercepterDemo;
import com.javaproref.kafka.apidemo.producer.ProducerPartitionerDemo;
import com.javaproref.kafka.apidemo.producer.ProducerSerializationDemo;
import com.javaproref.kafka.apidemo.tranactions.*;

import java.util.concurrent.ExecutionException;

public class Main {
    // bootstrap server  默认值，非默认值可通过第二个参数传入
    public static final String BOOT_STRAP_SEVERS = "CentOSA:9092,CentOSB:9092,CentOSC:9092";

    // demo主题，通过第一个参数传入
    public static final String CLEAR                     = "clear";                     // 清理实验环境
    public static final String INIT                      = "init";                      // 初始化实验环境
    public static final String TOPIC_DML                 = "topic_dml";                 // topic管理演示
    public static final String PRODUCER                  = "producer";                  // 生产者演示
    public static final String PRODUCER_ROUND_ROBIN      = "producer_round_robin";      // 生产者不使用record key，采用轮询的方式为record分区
    public static final String PRODUCER_PARTITIONER      = "producer_partitioner";      // 自定义生产者Partitioner
    public static final String PRODUCER_INTERCEPTOR      = "producer_interceptor";      // 自定义生产者消息拦截器
    public static final String CONSUMER                  = "consumer";                  // 消费者演示
    public static final String ASSIGN_CONSUMER_PARTITION = "assign_consumer_partition"; // 手动指定消费者消费的分区和消费其实位置
    public static final String OFFSET_LATEST             = "offset_latest";             // 指定新加入消费组起始偏移量为"latest"
    public static final String OFFSET_EARLIEST           = "offset_earliest";           // 指定新加入消费组起始偏移量为"earliest"
    public static final String OFFSET_NONE               = "offset_none";               // 指定新加入消费组起始偏移量为"none"
    public static final String OFFSET_CONSUMER_SUBMIT    = "offset_consumer_submit";    // 由消费者代码调用api提交offset，而非由框架定期自动提交
    public static final String SERIALIZATION_PRODUCER    = "serialization_producer";    // 自定义序列化的生产者
    public static final String SERIALIZATION_CONSUMER    = "serialization_consumer";    // 自定义序列化的消费者
    public static final String ACK_TIMEOUT_PRODUCER      = "ack_timeout_producer";      // ACK包超时Demo的生产者
    public static final String ACK_IDEMPOTENCE_PRODUCER  = "act_idempotence_producer";  // ACK包幂等性Demo的生产者
    public static final String ACK_CONSUMER              = "ack_consumer";              // ACK包相关Demo的消费者
    public static final String PROD_TRX_PRODUCER         = "prod_trx_producer";         // Producer Only事务Demo的生产者
    public static final String PROD_TRX_CONSUMER_RD_COMMITTED   = "prod_trx_consumer_read_committed";   // Producer Only事务Demo的消费者
    public static final String PROD_TRX_CONSUMER_RD_UNCOMMITTED = "prod_trx_consumer_read_uncommitted"; // Producer Only事务Demo的消费者
    public static final String CNP_TRX_PRODUCER          = "cnp_trx_producer";          // Consumer&Producer事务Demo的生产者
    public static final String CNP_TRX_PRODUCER_ABORT    = "cnp_trx_producer_abort";    // Consumer&Producer事务Demo的生产者，中途会触发事务终止
    public static final String CNP_TRX_FORWARD_NODE      = "cnp_trx_forward_node";      // Consumer&Producer事务Demo的转发节点，它既是生产者又是消费者
    public static final String CNP_TRX_FORWARD_ABORT     = "cnp_trx_forward_node_abort";// Consumer&Producer事务Demo的转发节点，它既是生产者又是消费者
    public static final String CNP_TRX_CONSUMER          = "cnp_trx_consumer";          // Consumer&Producer事务Demo的消费者

    // 打印帮助信息
    private static void printHelp() {
        System.out.println(
                "usage: "
                        + "\n\t" + "java -jar ${jar_file_path} ${demo_name} ${bootstrap_servers}\n\n" +
                        "bootstrap_servers: "
                        + "\n\t optional parameter, default value is : CentOSA:9092,CentOSB:9092,CentOSC:9092" +
                        "demo_name: "
                        + "\n\t" + Main.CLEAR
                        + "\n\t" + Main.INIT
                        + "\n\t" + Main.TOPIC_DML
                        + "\n\t" + Main.PRODUCER
                        + "\n\t" + Main.PRODUCER_ROUND_ROBIN
                        + "\n\t" + Main.PRODUCER_PARTITIONER
                        + "\n\t" + Main.PRODUCER_INTERCEPTOR
                        + "\n\t" + Main.CONSUMER
                        + "\n\t" + Main.ASSIGN_CONSUMER_PARTITION
                        + "\n\t" + Main.OFFSET_LATEST
                        + "\n\t" + Main.OFFSET_EARLIEST
                        + "\n\t" + Main.OFFSET_NONE
                        + "\n\t" + Main.OFFSET_CONSUMER_SUBMIT
                        + "\n\t" + Main.SERIALIZATION_PRODUCER
                        + "\n\t" + Main.SERIALIZATION_CONSUMER
                        + "\n\t" + Main.ACK_TIMEOUT_PRODUCER
                        + "\n\t" + Main.ACK_IDEMPOTENCE_PRODUCER
                        + "\n\t" + Main.ACK_CONSUMER
                        + "\n\t" + Main.PROD_TRX_PRODUCER
                        + "\n\t" + Main.PROD_TRX_CONSUMER_RD_COMMITTED
                        + "\n\t" + Main.PROD_TRX_CONSUMER_RD_UNCOMMITTED
                        + "\n\t" + Main.CNP_TRX_PRODUCER
                        + "\n\t" + Main.CNP_TRX_PRODUCER_ABORT
                        + "\n\t" + Main.CNP_TRX_FORWARD_NODE
                        + "\n\t" + Main.CNP_TRX_FORWARD_ABORT
                        + "\n\t" + Main.CNP_TRX_CONSUMER
        );
    }

    // 主函数
    public static void main(String[] args) {
        // Stream.of(args).forEach(System.out::println);
        if (null == args || args.length < 1) {
            Main.printHelp();
            return;
        }

        // boot strap servers
        String bootStrapSevers = (args.length >= 2) ? args[1] : BOOT_STRAP_SEVERS;

        // run demo
        try {
            switch (args[0]) {
                case Main.TOPIC_DML:
                    (new KafkaTopicDMLDemo()).runDemo(bootStrapSevers);
                    break;
                case Main.CLEAR:
                    (new RemoveTestingTopic()).runDemo(bootStrapSevers);
                    break;
                case Main.INIT:
                    (new CreateTestingTopic()).runDemo(bootStrapSevers);
                    break;
                case Main.PRODUCER:
                    (new KafkaProducerDemo()).runDemo(bootStrapSevers, RecordKeyPolicy.ENABLE);
                    break;
                case Main.PRODUCER_ROUND_ROBIN:
                    (new KafkaProducerDemo()).runDemo(bootStrapSevers, RecordKeyPolicy.DISABLE);
                    break;
                case Main.PRODUCER_PARTITIONER:
                    (new ProducerPartitionerDemo()).runDemo(bootStrapSevers, RecordKeyPolicy.ENABLE);
                    break;
                case Main.PRODUCER_INTERCEPTOR:
                    (new ProducerIntercepterDemo()).runDemo(bootStrapSevers);
                    break;
                case Main.CONSUMER:
                    (new KafkaConsumerSubscribeDemo()).runDemo(bootStrapSevers);
                    break;
                case Main.ASSIGN_CONSUMER_PARTITION:
                    (new KafkaConsumerPartitionAssignDemo()).runDemo(bootStrapSevers);
                    break;
                case Main.OFFSET_EARLIEST:
                    (new AutoOffsetResetConfigDemo()).runDemo(bootStrapSevers, AutoOffsetResetConfigDemo.EARLIEST);
                    break;
                case Main.OFFSET_LATEST:
                    (new AutoOffsetResetConfigDemo()).runDemo(bootStrapSevers, AutoOffsetResetConfigDemo.LATEST);
                    break;
                case Main.OFFSET_NONE:
                    (new AutoOffsetResetConfigDemo()).runDemo(bootStrapSevers, AutoOffsetResetConfigDemo.NONE);
                    break;
                case Main.OFFSET_CONSUMER_SUBMIT:
                    (new OffsetConsumerSubmitDemo()).runDemo(bootStrapSevers);
                    break;
                case Main.SERIALIZATION_PRODUCER:
                    (new ProducerSerializationDemo()).runDemo(bootStrapSevers);
                    break;
                case Main.SERIALIZATION_CONSUMER:
                    (new ConsumerDeserializationDemo()).runDemo(bootStrapSevers);
                    break;
                case Main.ACK_TIMEOUT_PRODUCER:
                    (new AckTimeoutDemoProducer()).runDemo(bootStrapSevers);
                    break;
                case Main.ACK_IDEMPOTENCE_PRODUCER:
                    (new IdempotenceDemoProducer()).runDemo(bootStrapSevers);
                    break;
                case Main.ACK_CONSUMER:
                    (new AckDemoConsumer()).runDemo(bootStrapSevers);
                    break;
                case Main.PROD_TRX_PRODUCER:
                    (new POnlyTrxDemoProducer()).runDemo(bootStrapSevers);
                    break;
                case Main.PROD_TRX_CONSUMER_RD_COMMITTED:
                    (new POnlyTrxDemoConsumer()).setEnableReadCommitted(true).runDemo(bootStrapSevers);
                    break;
                case Main.PROD_TRX_CONSUMER_RD_UNCOMMITTED:
                    (new POnlyTrxDemoConsumer()).setEnableReadCommitted(false).runDemo(bootStrapSevers);
                    break;
                case Main.CNP_TRX_PRODUCER:
                    (new CNPTrxDemoProducer()).setTriggerTrxAbort(false).runDemo(bootStrapSevers);
                    break;
                case Main.CNP_TRX_PRODUCER_ABORT:
                    (new CNPTrxDemoProducer()).setTriggerTrxAbort(true).runDemo(bootStrapSevers);
                    break;
                case Main.CNP_TRX_FORWARD_NODE:
                    (new CNPTrxDemoProducerAndConsumer()).setTriggerAbort(false).runDemo(bootStrapSevers);
                    break;
                case Main.CNP_TRX_FORWARD_ABORT:
                    (new CNPTrxDemoProducerAndConsumer()).setTriggerAbort(true).runDemo(bootStrapSevers);
                    break;
                case Main.CNP_TRX_CONSUMER:
                    (new CNPTrxDemoConsumer()).runDemo(bootStrapSevers);
                    break;
                default:
                    Main.printHelp();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
