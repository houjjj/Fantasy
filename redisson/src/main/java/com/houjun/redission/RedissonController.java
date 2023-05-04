package com.houjun.redission;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @Author: houjun
 * @Date: 2023/5/4 - 9:58
 * @Description:
 */
@RestController
public class RedissonController {

    @Autowired
    RedissonClient redissonClient;

    // 分布式id
    @GetMapping("/getautoid")
    public String getAutoId() {
        //  格式化格式为年月日
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        // 获取当前时间
        String now = LocalDate.now().format(formatter);
        // 通过redisson的自增获取序号
        RAtomicLong atomicLong = redissonClient.getAtomicLong(now);
        atomicLong.expire(1, TimeUnit.DAYS);
        // 拼装订单号
        return now + "" + atomicLong.incrementAndGet();
    }





}
