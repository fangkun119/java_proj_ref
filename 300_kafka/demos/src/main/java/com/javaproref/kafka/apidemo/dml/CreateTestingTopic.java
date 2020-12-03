package com.javaproref.kafka.apidemo.dml;

import com.javaproref.kafka.apidemo.common.DMLCommon;
import com.javaproref.kafka.apidemo.domain.Constants;
import org.apache.kafka.clients.admin.*;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreateTestingTopic {
    public void runDemo(String bootStrapServers) throws ExecutionException, InterruptedException {
        //1. 创建KafkaAdminClient
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        KafkaAdminClient adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);

        //2. 试验用的TOPIC列表
        int partitionNum = 3;
        short replicaFactor = 2;
        List<NewTopic> topics = Stream.of(Constants.DEMO_TOPICS).map(topicName -> {
            return new NewTopic(topicName, partitionNum, replicaFactor);
        }).collect(Collectors.toList());
        System.out.println("topics to create: ");
        topics.forEach(System.out::println);

        //3. 创建topics
        CreateTopicsResult createTopicResult = adminClient.createTopics(topics);
        createTopicResult.all().get(); //同步阻塞、等待topic创建完成

        //4. 查看Topic列表
        DMLCommon.getTopics(adminClient).forEach(System.out::println);

        //5. 关闭KafkaAdminClient
        adminClient.close();
    }
}
