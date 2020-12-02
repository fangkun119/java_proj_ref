package com.javaproref.kafka.apidemo.dml;

import com.javaproref.kafka.apidemo.common.DMLCommon;
import org.apache.kafka.clients.admin.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.sleep;

/**
 * Kafka Topic管理及DML
 * 需要在运行环境中（如Mac或者Linux的"/etc/hosts"）配置CentOSA, CentOSB, CentOSC的IP和主机名映射
 */
public class KafkaTopicDMLDemo {
    public void runDemo(String bootStrapServers) throws ExecutionException, InterruptedException {
        final String TOPIC_NAME = "dml_test_topic";
        final int PARTITION_NUM = 3;
        final short REPLICA_FACTOR = 2;

        //1. 创建KafkaAdminClient
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        KafkaAdminClient adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);

        //2. 列出topic
        System.out.println("before create topic: " + TOPIC_NAME);
        DMLCommon.getTopics(adminClient).forEach(System.out::println);

        //3. 异步提交Topic创建请求
        CreateTopicsResult createTopicResult = adminClient.createTopics(
                    Arrays.asList(
                        new NewTopic(TOPIC_NAME, PARTITION_NUM, REPLICA_FACTOR)
                    )
                );

        //3. 同步阻塞、等待topic创建完成
        createTopicResult.all().get();

        //4. 查看Topic列表
        System.out.println("after create topic: " + TOPIC_NAME);
        DMLCommon.getTopics(adminClient).forEach(System.out::println);

        //5. 查看Topic详细信息
        DescribeTopicsResult dtr = adminClient.describeTopics(Arrays.asList(TOPIC_NAME));
        Map<String, TopicDescription> topicDescriptionMap = dtr.all().get();
        topicDescriptionMap.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        });

        //6. 异步发起删除Topic的请求
        DeleteTopicsResult deleteTopics
                = adminClient.deleteTopics(Arrays.asList(TOPIC_NAME));

        //7. 同步阻塞、等待Topic删除完毕
        deleteTopics.all().get();

        //8. 再次查看Tipic列表
        System.out.println("after delete topic: " + TOPIC_NAME);
        DMLCommon.getTopics(adminClient).forEach(System.out::println);

        // 关闭KafkaAdminClient
        adminClient.close();
    }
}
