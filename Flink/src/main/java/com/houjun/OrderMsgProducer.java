package com.houjun;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.List;

@Slf4j
public class OrderMsgProducer {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment("localhost",8081);
        env.enableCheckpointing(5000);

        env.fromSequence(1, 20) // 生成 20 条测试数据
                .map(new RichMapFunction<Long, String>() {
                    private transient DefaultMQProducer producer; // 一定要 transient

                    @Override
                    public void open(Configuration parameters) throws Exception {
//                        dev
//                        AclClientRPCHook auth = new AclClientRPCHook(new SessionCredentials("j7Ice4pk8t8r5SZl", "7y477G8CbGWVpKTltd5fExIdDnS8dz"));
//                        producer = new DefaultMQProducer("flink-test-producer", auth);
//                        producer.setNamesrvAddr("onsaddr.cn-beijing-zgyzkfy-d01.mq.namesrv.res.devcloud.cpct.cn:9876");
                        AclClientRPCHook auth = new AclClientRPCHook(new SessionCredentials("HhfigUpCuvpTOlo5", "AulWXB4UT5KYw5Vxx4ICNEflM2MikO"));
                        producer = new DefaultMQProducer("flink-test-producer", auth);
                        producer.setNamesrvAddr("onsaddr.cn-beijing-youzheng-d01.mq.namesrv.res.2020yzcloud.cn:9876");
                        producer.start();

                        log.info("Producer started.");
                    }

                    @Override
                    public String map(Long value) throws Exception {
                        String body = "Hello RocketMQ, msg=" + value;
                        String[] tags = new String[]{"ck_fifo"};
                        for (int i = 0; i < 100; i++) {
                            int orderId = i % 10;
                            Message msg = new Message("YCYT_SIT_PSC_CK_FIFO", tags[i % tags.length], "KEY" + i,
                                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                            SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                                @Override
                                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                                    Integer id = (Integer) arg;
                                    int index = id % mqs.size();
                                    return mqs.get(index);
                                }
                            }, orderId);

                            System.out.printf("%s%n", sendResult);
                        }
                        log.info("SEND OK => {}", body);
                        return body;
                    }

                    @Override
                    public void close() throws Exception {
                        if (producer != null) {
                            producer.shutdown();
                        }
                        log.info("Producer shutdown.");
                    }
                }).name("发送MQ消息")
                .print();

        env.execute("Flink Order RocketMQ Test Job");
    }
}