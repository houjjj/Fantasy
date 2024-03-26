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
    String nameServAddrs = "192.168.12.76:32008";
    RocketmqTopicDetail detail = null;
    MQAdminExt mqAdminExt = null;
    String topicName = "helloworld";
    String username = "llLL1111";
    String password = "llLL1111";
    String clusterName = "ns-ll";

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
//        mqAdminExt = RocketmqAdminService.getAdminExt();
        detail = new RocketmqTopicDetail();
        detail.setNameServAddrs(nameServAddrs);
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
}