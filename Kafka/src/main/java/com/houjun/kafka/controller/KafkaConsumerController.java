package com.houjun.kafka.controller;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerController {

    @KafkaListener(topicPattern = "topictest.*", groupId = "myGroup")
    public void consume(String message) {
        // Print statement
        System.out.println("consumer message = " + message);

    }
}