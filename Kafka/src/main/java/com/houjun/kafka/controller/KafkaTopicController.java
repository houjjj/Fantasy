package com.houjun.kafka.controller;

import com.houjun.kafka.domain.SimpleKafkaTopicInfo;
import com.houjun.kafka.mqadmin.KafkaAdminService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController()
@RequestMapping("/topic")
@Slf4j
public class KafkaTopicController {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.username:''}")
    String username = "";
    @Value("${spring.kafka.password:''}")
    String password = "";
    @GetMapping("/{name}")
    public void sendMessage(@PathVariable("name") String name) throws ExecutionException, InterruptedException {
        AdminClient client = KafkaAdminService.getClient(bootstrapServers, username, password);
        NewTopic newTopic = new NewTopic(name, 2, (short) 2);
        CreateTopicsResult res = client.createTopics(Collections.singletonList(newTopic));
        res.all().get();
    }

    @GetMapping("/list")
    public List<SimpleKafkaTopicInfo> listTopic() throws ExecutionException, InterruptedException {
        AdminClient client = KafkaAdminService.getClient(bootstrapServers, username, password);
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
        return res;
    }
}
