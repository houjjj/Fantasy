package com.houjun.rocketmq.domain;

import com.houjun.rocketmq.utils.TimeUtil;
import lombok.Data;

@Data
public class RocketmqTopicDetail {
    
    private String clusterName;
    private String brokerName;
    private String topicName;
    private String nameServAddrs;
    
    private String consumerGroup;
    
    private int readQueueNums;
    private int writeQueueNums;
    private int perm;
    
    private Long resetTimestamp;
    private boolean isForce = true;
    
    // 防止多次调用
    boolean postProcessed = false;
    // 前台传过来的时间是 yyyyMMddhhmmss 格式的字符串，被 spring 处理成 long，需要处理成真正的 long
    public void postProcessTime() {
        if (postProcessed) {
            return;
        }
        postProcessed = true;
        if (resetTimestamp != null) {
            resetTimestamp = TimeUtil.formatToLong(String.valueOf(resetTimestamp));
        }
    }
}
