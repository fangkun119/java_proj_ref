package com.javaproref.kafka.apidemo.demo01.dml;

import org.apache.kafka.clients.admin.*;

import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.sleep;

/**
 * Kafka Topic管理及DML
 * 需要在运行环境中（如Mac或者Linux的"/etc/hosts"）配置CentOSA, CentOSB, CentOSC的IP和主机名映射
 */
public class KafkaTopicDML {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final String TOPIC_NAME = "dmlTestTopic01";
        final int PARTITION_NUM = 3;
        final short REPLICA_FACTOR = 2;

        //1. 创建KafkaAdminClient
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                "CentOSA:9092,CentOSB:9092,CentOSC:9092");
        KafkaAdminClient adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);

        //2. 列出topic
        System.out.println("before create topic: " + TOPIC_NAME);
        listTopics(adminClient);

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
        listTopics(adminClient);

        //5. 异步发起删除Topic的请求
        DeleteTopicsResult deleteTopics = adminClient.deleteTopics(Arrays.asList(TOPIC_NAME));

        //6. 同步阻塞、等待Topic删除完毕
        deleteTopics.all().get();

        //7. 再次查看Tipic列表
        System.out.println("after delete topic: " + TOPIC_NAME);
        listTopics(adminClient);

        // 关闭KafkaAdminClient
        adminClient.close();
    }

    private static void listTopics(KafkaAdminClient adminClient) throws ExecutionException, InterruptedException {
        ListTopicsResult topicsResult = adminClient.listTopics();
        Set<String> names = null;
        names = topicsResult.names().get();
        for (String name: names) {
            System.out.println(name);
        }
    }
}
