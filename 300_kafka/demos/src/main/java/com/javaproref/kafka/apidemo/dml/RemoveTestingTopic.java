package com.javaproref.kafka.apidemo.dml;

import com.javaproref.kafka.apidemo.common.DMLCommon;
import com.javaproref.kafka.apidemo.domain.Constants;
import org.apache.kafka.clients.admin.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Kafka Topic管理及DML
 */
public class RemoveTestingTopic {
    public void runDemo(String bootStrapServers) throws ExecutionException, InterruptedException {
        //1. 创建KafkaAdminClient
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        KafkaAdminClient adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);

        //2. 要删除的topic列表
        List<String> topics = topicsToDel(adminClient, Constants.DEMO_TOPICS);

        //3. 删除
        DeleteTopicsResult delRst = adminClient.deleteTopics(topics);
        delRst.all().get(); //阻塞等待异步delete完成

        //4. 查看Topic列表
        DMLCommon.getTopics(adminClient).forEach(System.out::println);

        //5. 关闭KafkaAdminClient
        adminClient.close();
    }

    private List<String> topicsToDel(KafkaAdminClient adminClient, String[] allTopics) throws ExecutionException, InterruptedException {
        List<String> topics = new ArrayList<>();
        for (String topic : allTopics) {
            // 如果存在"TOPIC"则删除
            if (DMLCommon.getTopics(adminClient).contains(topic)) {
                topics.add(topic);
            }
        }
        System.out.println("topics to delete:");
        topics.forEach(System.out::println);
        return topics;
    }
}
