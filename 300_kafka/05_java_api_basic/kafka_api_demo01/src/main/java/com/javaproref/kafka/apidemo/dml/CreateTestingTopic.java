package com.javaproref.kafka.apidemo.dml;

import com.javaproref.kafka.apidemo.util.Constants;
import org.apache.kafka.clients.admin.*;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class CreateTestingTopic {
    public void runDemo(String bootStrapServers) throws ExecutionException, InterruptedException {
        final String TOPIC_01 = Constants.TOPIC_01;
        final int PARTITION_NUM = 3;
        final short REPLICA_FACTOR = 2;

        //1. 创建KafkaAdminClient
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        KafkaAdminClient adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);

        //3. 创建"topic01"
        System.out.println("create " + TOPIC_01);
        CreateTopicsResult createTopicResult
                = adminClient.createTopics(Arrays.asList(
                new NewTopic(TOPIC_01, PARTITION_NUM, REPLICA_FACTOR)));
        createTopicResult.all().get(); //同步阻塞、等待topic创建完成

        //4. 查看Topic列表
        DMLCommon.getTopics(adminClient).forEach(System.out::println);

        //5. 关闭KafkaAdminClient
        adminClient.close();
    }
}
