package com.houjun.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @PostMapping("/hello")
    public String hello() {
        return "world";
    }
    @PostMapping("/exception")
    public String exception() {
        throw new RuntimeException("asdfsad");
    }
}
