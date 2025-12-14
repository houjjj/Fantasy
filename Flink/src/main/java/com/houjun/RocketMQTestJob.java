package com.houjun;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

@Slf4j
public class RocketMQTestJob {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);

        env.fromSequence(1, 20) // 生成 20 条测试数据
                .map(new RichMapFunction<Long, String>() {
                    private transient DefaultMQProducer producer; // 一定要 transient

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        AclClientRPCHook auth = new AclClientRPCHook(new SessionCredentials("rocketmq", "JdgzgcUat@mq0929"));
                        producer = new DefaultMQProducer("flink-test-producer", auth);
                        producer.setNamesrvAddr("10.2.49.49:30139"); // ← 修改为你自己的
                        producer.start();
                        log.info("Producer started.");
                    }

                    @Override
                    public String map(Long value) throws Exception {
                        String body = "Hello RocketMQ, msg=" + value;
                        Message msg = new Message(
                                "topictest",     // ← 修改为你的 Topic
                                "test_tag",
                                body.getBytes()
                        );
                        producer.send(msg);
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

        env.execute("Flink RocketMQ Test Job");
    }
}
