package com.houjun.rocketmq.service.v2;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 消费者
@Service
@ConditionalOnProperty(name = "rocketmq.name-server")
@RocketMQMessageListener(topic = "test-rocket-topic", consumerGroup = "boot-consumer-group")
class RocketConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        System.out.println("收到RocketMQ消息: " + message);
    }
}