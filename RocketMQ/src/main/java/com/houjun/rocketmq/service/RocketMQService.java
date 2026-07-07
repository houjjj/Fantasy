package com.houjun.rocketmq.service;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RocketMQService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    // 生产者
    public void send(String topic, String msg) {
        rocketMQTemplate.convertAndSend(topic, msg);
    }
}

