package com.houjun.controller;

import com.houjun.domain.Memory_by_thread_by_current_bytes;
import com.houjun.domain.User;
import com.houjun.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/add")
    public List<User> add(@RequestParam("num") int num) {
        return userService.add(num);
    }
    @GetMapping("/get")
    public List<User> add() {
        return userService.get();
    }
}
