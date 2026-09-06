package com.houjun.rocketmq.controller;

import com.houjun.rocketmq.service.v2.RocketMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/producer/v2") // 加上这个注解来定义类级别的路径前缀
@ConditionalOnProperty(name = "rocketmq.name-server")
public class ProducerV2Controller {

    @Autowired
    RocketMQService rocketMQService;

    @GetMapping("send")
    public String producer(@RequestParam(value = "topic") String topic,
                           @RequestParam(value = "msg") String msg) {
        rocketMQService.send(topic, msg);
        return "ok";
    }

}
