package com.houjun.mybatis.mapper;

import com.houjun.mybatis.domain.HR;
import com.houjun.mybatis.domain.Menu;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author HouJun
 * @date 2021-12-25 15:38
 */
@Mapper
@CacheNamespace
public interface MenuMapper {

    @Select("select * from menu where name=#{i111111d}")
    Menu selectByName(String name);

    @Select("select * from menu")
    List<Menu> selectList();

    @Select("select * from menu where id=#{id}")
//    @Options(flushCache = Options.FlushCachePolicy.FALSE)
    List<Menu> selectById(Integer id);

    @Update("update menu set name=#{name} where id=#{id}")
    void update(@Param("id") Integer id,@Param("name") String name);

}
