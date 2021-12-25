package com.houjun.mybatis.mapper;

import com.houjun.mybatis.HR;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author HouJun
 * @date 2021-12-25 15:38
 */
@Mapper
public interface HRMapper {
//    @Insert("select * from hr where id=#{id}")
    HR selectById(@Param("id") Integer id);
}
