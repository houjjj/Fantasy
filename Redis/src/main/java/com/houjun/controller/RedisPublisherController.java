package com.houjun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisPublisherController {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @GetMapping("/pub")
    public void publish(String msg){

        redisTemplate.convertAndSend("cache:redis:caffeine:topic",msg);

    }

}