package com.houjun.rocketmq.domain;

 import com.houjun.rocketmq.utils.TimeUtil;
 import lombok.Data;

@Data
public class RocketmqMessageQuery {

    private Long startTime;
    private Long endTime;
//    private int pageNum;
//    private int pageSize;
    private String topicName;
    
    private String consumerGroup;
    private String nameServAddrs;
    
    // 防止多次调用
    boolean postProcessed = false;
    // 前台传过来的时间是 yyyyMMddhhmmss 格式的字符串，被 spring 处理成 long，需要处理成真正的 long
    public void postProcessTime() {
        if (postProcessed) {
            return;
        }
        postProcessed = true;
        if (startTime != null) {
            startTime = TimeUtil.formatToLong(String.valueOf(startTime));
        }
        if (endTime != null) {
            endTime = TimeUtil.formatToLong(String.valueOf(endTime));
        }
    }
}
