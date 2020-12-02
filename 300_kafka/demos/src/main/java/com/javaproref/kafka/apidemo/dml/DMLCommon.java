package com.javaproref.kafka.apidemo.dml;

import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class DMLCommon {
    public static Set<String> getTopics(KafkaAdminClient adminClient) throws ExecutionException, InterruptedException {
        ListTopicsResult topicsResult = adminClient.listTopics();
        Set<String> names = null;
        names = topicsResult.names().get();
        return names;
    }
}
