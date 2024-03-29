package com.houjun.rocketmq.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @Author: houjun
 * @Date: 2023/4/27 - 10:40
 * @Description:
 */
@RestController
public class ProducerController {

    @Value("${rocketmq.url}")
    private String url;
    @Value("${rocketmq.username}")
    private String username;
    @Value("${rocketmq.password}")
    private String password;
    @Value("${rocketmq.topic}")
    private String topic;
    @Value("${rocketmq.producerGroup}")
    private String producerGroup;


    @GetMapping("/producer")
    public String producer() throws MQClientException, UnsupportedEncodingException, InterruptedException, RemotingException {
        DefaultMQProducer producer;
        if (StringUtils.isEmpty(username)) {
            producer = new DefaultMQProducer(producerGroup);
        } else {
            AclClientRPCHook auth = new AclClientRPCHook(new SessionCredentials(username, password));
            producer = new DefaultMQProducer(producerGroup, auth);
        }
        // 设置NameServer的地址
        producer.setNamesrvAddr(url);

        // 启动Producer实例
        producer.start();
        producer.setRetryTimesWhenSendAsyncFailed(0);

        int messageCount = 1;
        // 根据消息数量实例化倒计时计算器
        final CountDownLatch2 countDownLatch = new CountDownLatch2(messageCount);
        for (int i = 0; i < messageCount; i++) {
            final int index = i;
            // 创建消息，并指定Topic，Tag和消息体
            Message msg = new Message(topic,
                    "TagA",
                    "Key1",
                    ("Hello world  " + LocalDateTime.now()).getBytes(RemotingHelper.DEFAULT_CHARSET));
            // SendCallback接收异步返回结果的回调
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    countDownLatch.countDown();
                    System.out.printf("%-10d OK %s %n", index,
                            sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    countDownLatch.countDown();
                    System.out.printf("%-10d Exception %s %n", index, e);
                    e.printStackTrace();
                }
            });
        }
        // 等待5s
        countDownLatch.await(5, TimeUnit.SECONDS);
        // 如果不再发送消息，关闭Producer实例。
        producer.shutdown();
        return "send successful";
    }
}
