package com.houjun.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AsyncTask {
    @Autowired
    RedisTemplate redisTemplate;

    @SneakyThrows
    @Async("asyncPoolTaskExecutor")
    public void doTask1(String key) {
        long t1 = System.currentTimeMillis();
        redisTemplate.boundValueOps(key).expire(3600, TimeUnit.SECONDS);
        long t2 = System.currentTimeMillis();
        log.info("task1 cost {} ms" , t2-t1);
    }

    @SneakyThrows
    @Async
    public void doTask2() {
        long t1 = System.currentTimeMillis();
        Thread.sleep(3000);
        long t2 = System.currentTimeMillis();
        log.info("task2 cost {} ms" , t2-t1);
    }
}
