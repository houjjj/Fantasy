package com.houjun.opengauss.mapper;

import com.houjun.opengauss.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM users WHERE id = #{id}")
    User selectById(Long id);

    @Insert("INSERT INTO users(username, email, age) VALUES(#{username}, #{email}, #{age})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
}