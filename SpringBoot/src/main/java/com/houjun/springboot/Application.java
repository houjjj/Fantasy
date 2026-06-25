package com.houjun.springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.houjun.springboot.**"})
@MapperScan("com.houjun.springboot.mapper")
public class Application {

    public static void main(String[] args) {
        System.out.println("agent 服务启动成功！");
        SpringApplication.run(Application.class, args);
    }

}
