package com.houjun.rocketmq.controller;

import com.alibaba.fastjson.JSON;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

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
    @Value("${rocketmq.username:}")
    private String username;
    @Value("${rocketmq.password:}")
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
    public String producer(@RequestParam(value = "count", defaultValue = "10") Integer count)
            throws MQClientException, UnsupportedEncodingException, InterruptedException, RemotingException, MQBrokerException {
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
        producer.setSendMsgTimeout(50000);

        for (int i = 0; i < count; i++) {
            LocalDateTime now = LocalDateTime.now();
            // 创建消息，并指定Topic，Tag和消息体.
            String str = "Hello world  " + now;
            byte[] bytes = str.getBytes(RemotingHelper.DEFAULT_CHARSET);
            Message msg = new Message(topic,
                    "TagA",
                    "Key1",
                    bytes);

            SendResult send = producer.send(msg);
            System.out.printf("【producer】content:%s,offsetMsgId:%s \n", str, send.getOffsetMsgId());
            Thread.sleep(200);
        }
        // 如果不再发送消息，关闭Producer实例。;想要生产者在线，这里不能shutdown
//        producer.shutdown();
        return "send successful";
    }

    @GetMapping("/async")
    public String async()
            throws MQClientException, UnsupportedEncodingException, InterruptedException, RemotingException, MQBrokerException {
        DefaultMQProducer producer;
        if (StringUtils.isEmpty(username)) {
            producer = new DefaultMQProducer(producerGroup);
        } else {
            AclClientRPCHook auth = new AclClientRPCHook(new SessionCredentials(username, password));
            producer = new DefaultMQProducer(producerGroup, auth);
        }
        // 设置NameServer的地址
        producer.setNamesrvAddr(url);
        producer.setSendLatencyFaultEnable(true);
        // 启动Producer实例
        producer.start();

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
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
                    System.out.printf("producer %s %n", JSON.toJSON(sendResult));
                }

                @Override
                public void onException(Throwable e) {
                    System.out.printf("producer %-10d Exception %s %n", index, e);
                }
            });
            Thread.sleep(500);
        }
        // 如果不再发送消息，关闭Producer实例。
        producer.shutdown();
        return "send successful";
    }

    @GetMapping("/sync")
    public String batch()
            throws MQClientException, UnsupportedEncodingException, InterruptedException, RemotingException, MQBrokerException {
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
        producer.setSendMsgTimeout(6000);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            // 创建消息，并指定Topic，Tag和消息体
            Message msg = new Message(topic,
                    "TagA",
                    "Key1",
                    ("Hello world  " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));

            SendResult send = producer.send(msg);
            System.out.println("producer: offsetId=" + JSON.toJSON(send));
            Thread.sleep(200);// 每秒1条
        }
        // 如果不再发送消息，关闭Producer实例。
        producer.shutdown();
        return "send successful";
    }

    @GetMapping("/delay")
    public void delayProducer(@RequestParam(defaultValue = "1") Integer count) throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
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

        for (int i = 0; i < count; i++) {
            Message message = new Message(topic, ("Hello scheduled message " + i).getBytes());
            // This message will be delivered to consumer 10 seconds later.
            message.setDelayTimeLevel(delayLevel);
            // Send the message
            SendResult send = producer.send(message);
            System.out.println("producer delay " + JSON.toJSON(send));
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

        log.info("[getSshSession] username:{},host:{},password:{},port:{}", username, host, password, port);
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

    public static void main(String[] args) {
        byte[] msgBytes = JSON.toJSONString("{\"op\":\"c\",\"ts_ms\":1762139106580,\"data\":{\"id\":1985181413591453697,\"line_id\":null,\"cust_bill_no\":\"ACMDZ1437736486185093\",\"bill_flag\":\"1\",\"bill_no\":\"148925623498_86972157440\",\"src_system\":\"YWJD\",\"cust_code\":\"1100173909382\",\"cust_name\":\"井研县佳源农资经营部(柑橘)\",\"party_code\":null,\"cust_sub_code\":null,\"post_org_code\":\"61310701\",\"post_org_name\":\"中国邮政集团有限公司四川省井研县三江邮政所\",\"settle_org_code\":null,\"settle_org_name\":null,\"biz_date\":1759075200000,\"total_amount\":null,\"postage_paid\":5.000000,\"postage_other\":0.000000,\"payment_mode\":null,\"settlement_mode\":\"YCK\",\"is_freight_collect\":null,\"quantity\":null,\"sender_province_no\":null,\"sender_province_name\":null,\"sender_warehouse_id\":null,\"sender_warehouse_name\":null,\"product_type\":\"0\",\"account_book\":null,\"comp_code\":null,\"dept_code\":null,\"comp_name\":null,\"dept_name\":null,\"turned_account_book\":null,\"turned_comp_code\":null,\"turned_dept_code\":null,\"turned_comp_name\":null,\"turned_dept_name\":null,\"biz_product_no\":null,\"biz_product_name\":null,\"last_product_code\":null,\"last_product_name\":null,\"specialty_code\":null,\"specialty_name\":null,\"reserved1\":null,\"reserved2\":null,\"reserved3\":null,\"reserved4\":null,\"reserved5\":null,\"reserved6\":null,\"reserved7\":null,\"reserved8\":null,\"reserved9\":null,\"reserved10\":null,\"reserved11\":1,\"reserved12\":null,\"reserved13\":null,\"reserved14\":\"4\",\"reserved15\":null,\"reserved16\":null,\"reserved17\":null,\"reserved18\":null,\"reserved19\":null,\"reserved20\":null,\"create_date\":\"2025-11-03 11:05:06\",\"create_user\":null,\"update_date\":null,\"update_user\":null,\"is_deleted\":0,\"exception_type\":\"2\",\"exception_memo\":\"金额不一致\",\"status\":null,\"exception_count\":0,\"exception_amount\":5.00,\"is_unified\":null,\"parent_cust_bill_no\":null,\"bill_no_origin\":\"8432347384612\",\"send_bigdata_time\":\"2025-11-03 11:05:10\"},\"table\":\"acm_bill_exception_log\",\"timeVo\":{\"timestamp\":1762139110019,\"nowTime\":\"2025-11-03 11:05:10\"}}").getBytes(StandardCharsets.UTF_8);
        System.out.println("Message size: " + msgBytes.length + " bytes");
    }

}
