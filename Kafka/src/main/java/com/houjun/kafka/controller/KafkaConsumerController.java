package com.houjun.kafka.controller;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerController {

    @KafkaListener(topicPattern = "portal_access_log_topic_uat.*", groupId = "myGroup")
    public void consume(String message) {
        // Print statement
        System.out.println("consumer message = " + message);

    }
}