package com.houjun.controller;

import com.houjun.config.AsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.NumberUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author: houjun
 * @Date: 2024/1/30 - 12:36
 * @Description:
 */
@RestController
@Slf4j
public class HelloController {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    AsyncTask asyncTask;

    @GetMapping("/set")
    public void start(int count) throws InterruptedException {
        for (int i = 0; i < count; i++) {
            ValueOperations ops = redisTemplate.opsForValue();
            long start = System.currentTimeMillis();
            log.info("set start {}", start);
            ops.set("hello", "world");
            long end = System.currentTimeMillis();
            log.info("set end {} ,duration {},", end, (end - start) / 1000);
            Thread.sleep(2000);
        }
    }

    @GetMapping("/get")
    public void get(int count) throws InterruptedException {
        ValueOperations ops = redisTemplate.opsForValue();
        long start = System.currentTimeMillis();
        log.info("get start {}", start);
        for (int i = 0; i < count; i++) {
            System.out.println(ops.get("hello"));
            Thread.sleep(2000);
        }
        long end = System.currentTimeMillis();
        log.info("get end {} ,duration {},", end, (end - start) / 1000);
    }

    @GetMapping("/update")
    public void update(String key, int count, int thread) throws InterruptedException {
        long start = System.currentTimeMillis();
        log.info(" start {}", start);
        Set<Long> set = new HashSet<>();
        ExecutorService executorService = Executors.newFixedThreadPool(thread);
        for (int i = 0; i < count; i++) {
            int finalI = i;
            executorService.execute(() -> {
                log.info("order {}", finalI);
                long t1 = System.currentTimeMillis();
                redisTemplate.boundValueOps(key).expire(3600, TimeUnit.SECONDS);
                long t2 = System.currentTimeMillis();
                log.info("task1 cost {} ms", t2 - t1);
                set.add(t2 - t1);
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
            System.out.println("线程池没有关闭");
        }
        System.out.println("线程池已经关闭");
        long end = System.currentTimeMillis();
        log.info(" end {} ,duration {}s,max duration {}ms", end, (end - start) / 1000, Collections.max(set));
    }

    @GetMapping("/update2")
    public void update2(String key, int count) {
        long start = System.currentTimeMillis();
        log.info("get start {}", start);
        for (int i = 0; i < count; i++) {
            redisTemplate.boundValueOps(key).expire(3600, TimeUnit.SECONDS);
        }
        long end = System.currentTimeMillis();
        log.info("get end {} ,duration {},", end, (end - start) / 1000);
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/send")
    public void send() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            stringRedisTemplate.convertAndSend("subscribe", "发布信息" + i);
            log.info("pub  ");
            Thread.sleep(3000);
        }
    }


}
