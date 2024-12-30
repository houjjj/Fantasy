package com.houjun.rocketmq.controller;

import com.houjun.rocketmq.domain.RocketmqMessageQuery;
import com.houjun.rocketmq.domain.RocketmqMessageView;
import com.houjun.rocketmq.domain.RocketmqTopicDetail;
import com.houjun.rocketmq.domain.SimpleRocketmqTopicInfo;
import com.houjun.rocketmq.service.RocketmqAdminService;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: houjun
 * @Date: 2023/4/27 - 10:40
 * @Description:
 */
@RestController
@Slf4j
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
    @Value("${rocketmq.delayLevel:3}")
    private int delayLevel;
    @Autowired
    private RocketmqAdminService rocketmqAdminService;

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
        producer.setRetryTimesWhenSendAsyncFailed(3);

        int messageCount = 10;

        // 根据消息数量实例化倒计时计算器
//        final CountDownLatch2 countDownLatch = new CountDownLatch2(messageCount);
        for (int i = 0; i < messageCount; i++) {
            final int index = i;
            // 创建消息，并指定Topic，Tag和消息体
            Message msg = new Message(topic,
                    "TagA",
                    "Key1",
                    ("Hello world  " + index).getBytes(RemotingHelper.DEFAULT_CHARSET));

            // SendCallback接收异步返回结果的回调
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
//                    countDownLatch.countDown();
                    System.out.printf("producer %-10d OK %s %n", index, sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
//                    countDownLatch.countDown();
                    System.out.printf("producer %-10d Exception %s %n", index, e);
//                    e.printStackTrace();
                }
            });
            Thread.sleep(200);
        }
        // 等待5s
//        countDownLatch.await(5, TimeUnit.SECONDS);
        // 如果不再发送消息，关闭Producer实例。
        producer.shutdown();
        return "send successful";
    }



    @GetMapping("/delay")
    public void delayProducer() throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
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
        int totalMessagesToSend = 100;
        for (int i = 0; i < totalMessagesToSend; i++) {
            Message message = new Message(topic, ("Hello scheduled message " + i).getBytes());
            // This message will be delivered to consumer 10 seconds later.
            message.setDelayTimeLevel(delayLevel);
            // Send the message
            SendResult send = producer.send(message);
            System.out.println("发送延时消息：" + send.getMsgId());
        }
        producer.shutdown();
    }














    @GetMapping("/copy")
    public String copy() throws IOException, JSchException {
        log.info("[scp] 进入");
//        String scpCMD = "   ls " + "/root/baidu";
        String scpCMD = "sudo scp -o StrictHostKeyChecking=no -r " + "/home/admin/baidu admin@10.1.210.45:/home/admin/baidu";
        Session session = getSshSession("******", "******");
        // 创建ChannelExec对象
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        // 执行命令
        channelExec.setCommand(scpCMD);
        channelExec.setPty(true);
        InputStream in = channelExec.getInputStream();
        OutputStream out = channelExec.getOutputStream();
        channelExec.connect();

        byte[] tmp = new byte[1024];
        LocalDateTime startTime1 = LocalDateTime.now();
        while (startTime1.plusMinutes(60).isAfter(LocalDateTime.now())) {
            log.info("[doMigrate]  文件拷贝中now:{}", LocalDateTime.now());
            try {
                while (in.available() > 0) {
                    int read = in.read(tmp, 0, 1024);
                    String tips = new String(tmp, 0, read);
                    if (tips.contains("password")) {
                        log.info("[doMigrate] 输入服务器密码");
                        out.write(("******\n").getBytes());
                        out.flush();
                    }
                    log.info("[doMigrate] 文件拷贝中now:{},inpustream:{}", LocalDateTime.now(), tips);
                    if (read < 0) {
                        in.close();
                        break;
                    }
                }
                log.info("[doMigrate]  文件拷贝中now:{},channelExec.isClosed():{},channelExec.getExitStatus():{}", LocalDateTime.now(), channelExec.isClosed(), channelExec.getExitStatus());
                if (channelExec.isClosed() || channelExec.getExitStatus() == 0) {
                    if (in.available() > 0) continue;
                    int exitStatus = channelExec.getExitStatus();
                    log.info("[doMigrate]  文件拷贝结束,exitStatus:{}", exitStatus);
                    System.out.println("Exit-status: " + exitStatus);
                    break;
                }
                log.info("[doMigrate]  文件拷贝中,sleep");
                Thread.sleep(1000);
            } catch (Exception ee) {
                log.error("[doMigrate] 文件拷贝中,sleep", ee);
                ee.printStackTrace();
            }
        }
        channelExec.disconnect();
        session.disconnect();
        return "succeeded";
    }

    public Session getSshSession(String host, String password) throws JSchException {
        // 创建JSch对象
        JSch jsch = new JSch();
        // 设置SSH连接的参数
        String username = "admin";
        int port = 22;
        // 建立SSH连接

        log.info("[getSshSession] username:{},host:{},password:{},port:{}",username, host, password, port);
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        log.info("[getSshSession] isConnected:{}", session.isConnected());
        return session;
    }



    @GetMapping("/listTopic")
    void listTopic() {
        RocketmqTopicDetail detail = new RocketmqTopicDetail();
        detail.setNameServAddrs(url);
        List<SimpleRocketmqTopicInfo> simpleRocketmqTopicInfos = RocketmqAdminService.listTopic(detail, username, password);
        simpleRocketmqTopicInfos.forEach(System.out::println);
    }
    @GetMapping("/queryByTopic")
    void queryByTopic(@RequestParam("topicName") String topicName) {
        RocketmqMessageQuery query = new RocketmqMessageQuery();
        query.setNameServAddrs(url);
        query.setTopicName(topicName);
        List<RocketmqMessageView> rocketmqMessageViews = rocketmqAdminService.listDlqMessage(query, username, password);
        for (RocketmqMessageView rocketmqMessageView : rocketmqMessageViews) {
            System.out.println(rocketmqMessageView);
        }
     }


}
