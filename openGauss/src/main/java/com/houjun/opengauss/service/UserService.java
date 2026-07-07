package com.houjun.opengauss.service;


import com.houjun.opengauss.domain.User;
import com.houjun.opengauss.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    public int addUser(User user) {
        return userMapper.insert(user);
    }
}