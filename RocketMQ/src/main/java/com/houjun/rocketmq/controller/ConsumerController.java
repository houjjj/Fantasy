package com.houjun.rocketmq.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Author: houjun
 * @Date: 2023/4/27 - 10:40
 * @Description:
 */
@RestController
public class ConsumerController {

    @Value("${rocketmq.url}")
    private String url;
    @Value("${rocketmq.username}")
    private String username;
    @Value("${rocketmq.password}")
    private String password;
    @Value("${rocketmq.topic}")
    private String topic;
    @Value("${rocketmq.consumerGroup}")
    private String consumerGroup;
    @Value("${rocketmq.subExpression}")
    private String subExpression;

    @GetMapping("/consumer")
    public String consumer() throws MQClientException {
        // 实例化消费者
        DefaultMQPushConsumer consumer;
        if (StringUtils.isEmpty(username)) {
            consumer = new DefaultMQPushConsumer(consumerGroup);
        } else {
            AclClientRPCHook auth = new AclClientRPCHook(new SessionCredentials(username, password));
            consumer = new DefaultMQPushConsumer(consumerGroup, auth, new AllocateMessageQueueAveragely());
        }

        // 设置NameServer的地址
        consumer.setNamesrvAddr(url);

        // 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
        consumer.subscribe(topic, subExpression);
        // 注册回调实现类来处理从broker拉取回来的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    System.out.println(new String(msg.getBody(), StandardCharsets.UTF_8));
                }
                // 标记该消息已经被成功消费
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 启动消费者实例
        consumer.start();
        return "Consumer Started.";
    }
}
