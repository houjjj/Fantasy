package com.houjun.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableKafka
@SpringBootApplication
public class KafkaApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(KafkaApplication.class, args);
    }

}
