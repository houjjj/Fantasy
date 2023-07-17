package com.houjun.rocketmq.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class TimeUtil {
    
    private static final DateTimeFormatter FROM_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    public static long formatToLong(String timeString) {
        if (timeString == null)
            throw new IllegalArgumentException("时间为 null 无法格式化");
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
        return LocalDateTime.parse(timeString, FROM_FORMATTER).toInstant(ZoneOffset.ofTotalSeconds(timeZone.getRawOffset()/1000)).toEpochMilli();
    }
    
}
