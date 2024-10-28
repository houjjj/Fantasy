package com.houjun.com.controller;

import com.houjun.com.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @Autowired
    HelloService helloService;

    @GetMapping("/hello")
    String hello() {
        return helloService.hello();
    }

    @GetMapping("/exception")
    String exception() {
         helloService.exception();
        return "";
    }
}