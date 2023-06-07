package com.houjun.kafka.controller.domain;

import lombok.Data;

@Data
public class SimpleKafkaTopicInfo {

    private String name;
    private int partitions;
    private int replicationFactor;
    
}
