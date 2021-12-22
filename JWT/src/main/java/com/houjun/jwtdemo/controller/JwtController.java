package com.houjun.jwtdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtController {
    @GetMapping("/admin/hello")
    public String admin_hello(){
        return "hello";
    }

    @GetMapping("/user/hello")
    public String user_hello(){
        return "hello";
    }
}
