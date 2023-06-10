package com.houjun.kafka.huawei;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author huaweicloud DMS
 */
@Component
public class DmsKafkaProducer {
    /**
     * Topic名称,根据实际情况修改
     */
    public static final String TOPIC = "test_topic123";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 定时任务每5秒钟生产一条消息
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void send() {
        String message = String.format("{id:%s,timestamp:%s}", UUID.randomUUID().toString(), System.currentTimeMillis());
        kafkaTemplate.send(TOPIC, message);
        System.out.println("send finished, message = " + message);
    }
}
