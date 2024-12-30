package com.houjun.kafka.controller;

import com.houjun.kafka.model.KafkaSender;
import com.houjun.kafka.service.KafkaConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class KafkaProducerController {


    @Autowired
    private KafkaSender kafkaSender;
    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @GetMapping("{topic}/send/{msg}")
    public void sendMessage(@PathVariable("topic") String topic, @PathVariable("msg") String msg) {
        for (int i = 0; i < 1000; i++) {
            kafkaSender.send(topic, msg + i);
        }
    }


}