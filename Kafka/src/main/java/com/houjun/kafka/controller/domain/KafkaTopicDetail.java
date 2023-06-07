package com.houjun.kafka.controller.domain;

import lombok.Data;

@Data
public class KafkaTopicDetail {
    
    
    private String name;
    private int partitions;
    private int replicationFactor;
    
//    private String cleanupPolicy;
    private long retentionMs;
    private long retentionBytes;
    
    // partition/config
    private String alterType;
    
    // 逗号分隔的 broker ip/port
    private String bootstrapServers;
}
