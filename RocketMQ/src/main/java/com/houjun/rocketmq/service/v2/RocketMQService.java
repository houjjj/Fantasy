package com.houjun.rocketmq.service.v2;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "rocketmq.name-server")
public class RocketMQService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    // 生产者
    public void send(String topic, String msg) {
        rocketMQTemplate.convertAndSend(topic, msg);
    }
}

