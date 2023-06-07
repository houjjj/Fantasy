
// Java Program to Illustrate Kafka Consumer

package com.houjun.kafka.controller;

// Importing required classes

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component

// Class
public class KafkaConsumer {

    @KafkaListener(topics = "hello", groupId = "myGroup")
    public void consume(String message) {
        // Print statement
        System.out.println("message = " + message);
    }
}