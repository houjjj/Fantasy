package com.houjun.kafka.controller.mqadmin;

import com.houjun.kafka.controller.domain.SimpleKafkaTopicInfo;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: houjun
 * @Date: 2023/6/7 - 22:40
 * @Description:
 */
class KafkaAdminServiceTest {

    AdminClient client = null;

    @BeforeEach
    void setUp() {
        String bootstrapServers = "192.168.1.73:32012,192.168.1.73:32011,192.168.1.73:32013";
        String username = "houjun";
        String password = "Admin123";
        client = KafkaAdminService.getClient(bootstrapServers, username, password);
    }

    @Test
    void listTopic() {
        ListTopicsResult topics = client.listTopics();
        try {
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
        } catch (Exception e) {
//            throw new CustomException(600, "原因: " + e.getMessage());
        } finally {
            Closer.doFinal(client::close);
        }
    }

    @Test
    void getTopicDetail() {
    }

    @Test
    void deleteTopic() {
    }

    @Test
    void createTopic() {
    }

    @Test
    void alterTopic() {
    }
}