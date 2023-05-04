package com.houjun.springboot;

import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
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

    @GetMapping("/get")
    public String get() throws MQClientException, UnsupportedEncodingException, InterruptedException, RemotingException {
        // 实例化消费者

//        AclClientRPCHook auth = new AclClientRPCHook(new SessionCredentials(username, password));
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("houjun");

        // 设置NameServer的地址
        consumer.setNamesrvAddr(url);

        // 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
        consumer.subscribe("TopicTest", "*");
        // 注册回调实现类来处理从broker拉取回来的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                for (MessageExt msg : msgs) {
                    System.out.println(new String(msg.getBody(), StandardCharsets.UTF_8));
                }
                // 标记该消息已经被成功消费
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 启动消费者实例
        consumer.start();
        System.out.printf("Consumer Started.%n");
        return "Consumer Started.%n";
    }
}
