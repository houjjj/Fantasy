package com.houjun.mapper;

import com.houjun.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {


    @Update("insert into user (username) values(#{user.username})")
    void add(@Param("user") User user);

    @Select("select * from user")
    List<User> list();
}
