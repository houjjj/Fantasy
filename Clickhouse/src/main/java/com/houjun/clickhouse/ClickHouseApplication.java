package com.houjun.clickhouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClickHouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClickHouseApplication.class, args);
    }

}
