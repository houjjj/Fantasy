package com.houjun.rocketmq.domain;

import lombok.Data;

@Data
public class RocketmqDlqMessageRequest {

    private String topicName;
    
    private String consumerGroup;

    private String msgId;

    private String clientId;
    
    private String nameServAddrs;
}
