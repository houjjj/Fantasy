package com.houjun.kafka.controller;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class KafkaConsumerController {

    private AtomicLong count = new AtomicLong();
    private AtomicLong bytes = new AtomicLong();

    private long lastTime = System.currentTimeMillis();

    @KafkaListener(topicPattern = "netflow_topic.*")
    public void consume(String message) {
        count.incrementAndGet();
        bytes.addAndGet(message.getBytes(StandardCharsets.UTF_8).length);

        long now = System.currentTimeMillis();

        if (now - lastTime >= 1000) {
            System.out.printf(
                    "TPS=%d msg/s, Throughput=%.2f MB/s%n",
                    count.get(),
                    bytes.get () / 1024.0 / 1024.0);

            count.set(0);
            bytes.set(0);
            lastTime = now;
        }

    }
}