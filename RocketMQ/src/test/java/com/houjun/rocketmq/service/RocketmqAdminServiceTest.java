package com.houjun.rocketmq.service;

import com.houjun.rocketmq.domain.RocketmqTopicDetail;
import com.houjun.rocketmq.domain.SimpleRocketmqTopicInfo;
import org.apache.rocketmq.tools.admin.MQAdminExt;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @Author: houjun
 * @Date: 2023/6/12 - 15:35
 * @Description:
 */
class RocketmqAdminServiceTest {
    String nameServAddrs = "192.168.12.81:9876";
    RocketmqTopicDetail detail = null;
    MQAdminExt mqAdminExt = null;
    String topicName = "topictest";
    String username = "";
    String password = "";

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
//        mqAdminExt = RocketmqAdminService.getAdminExt();
        detail = new RocketmqTopicDetail();
        detail.setNameServAddrs(nameServAddrs);
    }

    @Test
    void createTopic() {
        detail.setTopicName(topicName);
        detail.setClusterName("hj-ns1747");
        RocketmqAdminService.createTopic(detail, username, password);
    }

    @Test
    void listTopic() {
        List<SimpleRocketmqTopicInfo> simpleRocketmqTopicInfos = RocketmqAdminService.listTopic(detail, username, password);
        simpleRocketmqTopicInfos.forEach(System.out::println);
    }
}