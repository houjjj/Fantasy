package com.houjun.com.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("provider")
public interface HelloService {
    @PostMapping("/hello")
    String hello();
    @PostMapping("/exception")
    String exception();
}