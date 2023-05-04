package com.houjun.rocketmq;

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

    @GetMapping("/add")
    public String get() throws MQClientException, UnsupportedEncodingException, InterruptedException, RemotingException {
        DefaultMQProducer producer;
        if (StringUtils.isNoneBlank(username, password)) {
            AclClientRPCHook auth = new AclClientRPCHook(new SessionCredentials(username, password));
            producer = new DefaultMQProducer("houjun", auth);
        } else {
            producer = new DefaultMQProducer("houjun");
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
            Message msg = new Message("TopicTest",
                    "TagA",
                    "OrderID188",
                    "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
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
        return "Hello world";
    }
}
