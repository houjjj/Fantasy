package com.houjun.opengauss.web;


import com.houjun.opengauss.domain.User;
import com.houjun.opengauss.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public int addUser(@RequestBody User user) {
        return userService.addUser(user);
    }
}