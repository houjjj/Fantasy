package com.houjun.kafka.mqadmin;

import com.houjun.kafka.domain.SimpleKafkaTopicInfo;
import org.apache.kafka.clients.admin.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @Author: houjun
 * @Date: 2023/6/7 - 22:40
 * @Description:
 */
class KafkaAdminServiceTest {

    AdminClient client = null;

    @BeforeEach
    void setUp() {
//        String bootstrapServers = "172.16.207.14:9092, 172.16.203.27:9092, 172.16.203.26:9092";
        String bootstrapServers = "192.168.12.82:9092,192.168.12.83:9092,192.168.12.86:9092";
        String username = "";
        String password = "";
        client = KafkaAdminService.getClient(bootstrapServers, username, password);
    }

    @Test
    void listTopic() throws ExecutionException, InterruptedException {
        ListTopicsResult topics = client.listTopics();
        List<SimpleKafkaTopicInfo> res = new ArrayList<>();
        Set<String> names = topics.names().get();
        Map<String, TopicDescription> map = new TreeMap<>(client.describeTopics(names).all().get());
        for (Map.Entry<String, TopicDescription> each : map.entrySet()) {
            SimpleKafkaTopicInfo info = new SimpleKafkaTopicInfo();
            res.add(info);
            info.setName(each.getKey());
            // 虽然点了很多点 但其实很安全，都至少有一个元素
            info.setPartitions(each.getValue().partitions().size());
            info.setReplicationFactor(each.getValue().partitions().get(0).replicas().size());
        }
        for (SimpleKafkaTopicInfo re : res) {
            System.out.println(re);
        }

    }

    @Test
    void getTopicDetail() {
    }

    @Test
    void deleteTopic() throws ExecutionException, InterruptedException {
        DeleteTopicsResult res = client.deleteTopics(Collections.singletonList("test_topic"));
        res.all().get();

    }

    @Test
    void createTopic() throws ExecutionException, InterruptedException {
        NewTopic newTopic = new NewTopic("zhangsan", 2, (short) 2);
        CreateTopicsResult res = client.createTopics(Collections.singletonList(newTopic));
        res.all().get();
    }

    @Test
    void alterTopic() {
    }
}