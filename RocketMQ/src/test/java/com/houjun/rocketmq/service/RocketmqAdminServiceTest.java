package com.houjun.rocketmq.service;

import com.houjun.rocketmq.domain.RocketmqTopicDetail;
import com.houjun.rocketmq.domain.SimpleRocketmqTopicInfo;
import org.apache.rocketmq.tools.admin.MQAdminExt;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 *
 * rocketmq是去连接nameserver
 * @Author: houjun
 * @Date: 2023/6/12 - 15:35
 * @Description:
 */
class RocketmqAdminServiceTest {
    String nameServAddress = "192.168.1.72:30038";
    RocketmqTopicDetail detail = null;
    MQAdminExt mqAdminExt = null;
    String topicName = "cctv";
    String username = "admin123";
    String password = "Admin@456";
    String clusterName = "ycc-mq";

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
//        mqAdminExt = RocketmqAdminService.getAdminExt();
        detail = new RocketmqTopicDetail();
        detail.setNameServAddrs(nameServAddress);
    }

    @Test
    void createTopic() {
        detail.setTopicName(topicName);
        detail.setClusterName(clusterName);
        RocketmqAdminService.createTopic(detail, username, password);
    }

    @Test
    void listTopic() {
        List<SimpleRocketmqTopicInfo> simpleRocketmqTopicInfos = RocketmqAdminService.listTopic(detail, username, password);
        simpleRocketmqTopicInfos.forEach(System.out::println);
    }

    @Test
    void listConsumerGroup() {
        List<String> strings = RocketmqAdminService.listConsumerGroup(nameServAddress, topicName, username, password);
        for (String string : strings) {
            System.out.println(string);
        }
    }
}