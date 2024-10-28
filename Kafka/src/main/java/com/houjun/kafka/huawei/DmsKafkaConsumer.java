package com.houjun.kafka.huawei;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author huaweicloud DMS
 */
//@Component
public class DmsKafkaConsumer {
    /**
     * Topic名称，根据实际情况修改
     */
    private static final String TOPIC = "test_topic123";

    @KafkaListener(topics = {TOPIC})
    public void listen(ConsumerRecord<String, String> record) {
        Optional<String> message = Optional.ofNullable(record.value());
        if (message.isPresent()) {
            System.out.println("consume finished, message = " + message.get());
        }
    }
}
