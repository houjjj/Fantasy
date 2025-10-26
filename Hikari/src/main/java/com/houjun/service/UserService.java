package com.houjun.service;

import com.houjun.domain.User;
import com.houjun.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public List<User> add(int num) {
        List<User> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            User user = new User();
            user.setUsername("张三" + i);
            list.add(user);
            userMapper.add(user);
        }
        return list;
    }

    public List<User> get() {
        return userMapper.list();
    }
}
