package com.houjun.kafka.controller;

import com.houjun.kafka.model.KafkaSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class KafkaController {


    @Autowired
    private KafkaSender kafkaSender;


    @GetMapping("sendMessage/{msg}")
    public void sendMessage(@PathVariable("msg") String msg) {
        for (int i = 0; i < 10000; i++) {
            kafkaSender.send(msg + i);
        }
    }

    @GetMapping("consumer/{msg}")
    public void consumer(@PathVariable("msg") String msg) {
        kafkaSender.send(msg);
    }
}