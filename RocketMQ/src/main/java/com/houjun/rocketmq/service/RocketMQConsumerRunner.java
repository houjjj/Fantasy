package com.houjun.rocketmq.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class RocketMQConsumerRunner {

    @Value("${rocketmq.url}")
    private String url;
    @Value("${rocketmq.username:}")
    private String username;
    @Value("${rocketmq.password:}")
    private String password;
    @Value("${rocketmq.topic}")
    private String topic;
    @Value("${rocketmq.consumerGroup}")
    private String consumerGroup;
    @Value("${rocketmq.subExpression}")
    private String subExpression;
    private DefaultMQPushConsumer consumer;

    // @PostConstruct 保证该方法在 Spring Boot 项目启动时【自动且仅执行一次】
    @PostConstruct
    public void init() throws MQClientException {
        if (StringUtils.isEmpty(username)) {
            consumer = new DefaultMQPushConsumer(consumerGroup);
        } else {
            AclClientRPCHook auth = new AclClientRPCHook(new SessionCredentials(username, password));
            consumer = new DefaultMQPushConsumer(consumerGroup, auth, new AllocateMessageQueueAveragely());
        }

        consumer.setNamesrvAddr(url);
        // 1. 显式调大向 NameServer 请求组件的 RPC 同步超时时间（默认 3000）
        consumer.setMqClientApiTimeout(10000); // 放大到 10 秒

        // 避坑关键 1：关闭 VIP 通道
        consumer.setVipChannelEnabled(false);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(topic, "*");

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    System.out.println("【consumed】content:" + new String(msg.getBody(), StandardCharsets.UTF_8));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        System.out.println("====== RocketMQ 消费者后台异步启动成功 ======");
    }

    // 项目停止时优雅关闭消费者
    @PreDestroy
    public void shutdown() {
        if (consumer != null) {
            consumer.shutdown();
            System.out.println("====== RocketMQ 消费者已优雅关闭 ======");
        }
    }
}