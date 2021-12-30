package com.houjun.mybatis.mapper;

import com.houjun.mybatis.domain.HR;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author HouJun
 * @date 2021-12-25 15:38
 */
@Mapper
//@CacheNamespace
public interface HRMapper {
    HR selectById(@Param("id") Integer id);

    @Select("select * from hr where name=#{name}")
    HR selectByName(@Param("name") String name);

    void updateName(@Param("id") Integer id, @Param("name") String name);
}
