package com.javaproref.kafka.apidemo.dml;

import com.javaproref.kafka.apidemo.common.DMLCommon;
import com.javaproref.kafka.apidemo.util.Constants;
import org.apache.kafka.clients.admin.*;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Kafka Topic管理及DML
 */
public class RemoveTestingTopic {
    public void runDemo(String bootStrapServers) throws ExecutionException, InterruptedException {
        final String TOPIC_01 = Constants.TOPIC_01;

        //1. 创建KafkaAdminClient
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        KafkaAdminClient adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);

        //2. 如果存在"topic01"则删除
        if (DMLCommon.getTopics(adminClient).contains(TOPIC_01)) {
            System.out.println("delete " + TOPIC_01);
            DeleteTopicsResult delRst = adminClient.deleteTopics(Arrays.asList(TOPIC_01));
            delRst.all().get(); //阻塞等待异步delete完成
        }

        //3. 查看Topic列表
        DMLCommon.getTopics(adminClient).forEach(System.out::println);

        //4. 关闭KafkaAdminClient
        adminClient.close();
    }
}
