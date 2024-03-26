package com.houjun.springboot.mapper;

import com.houjun.springboot.domain.CloudApp;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @Author: houjun
 * @Date: 2023/5/5 - 15:45
 * @Description:
 */
@Repository
@Mapper
public interface HelloMapper {

    @Update("update cloud_app set name=#{name},update_time=#{updateTime} where id=#{id}")
    int update(CloudApp app);

    @Insert("insert into  cloud_app (name,kube_id,namespace,status,is_deleted,create_time) value(#{name},#{kubeId},#{namespace},#{status},#{deleted}, #{createTime})")
    void insert(CloudApp app);

    @Select("select * from  cloud_app where id=#{id}")
    CloudApp get(int id);

    @Select("select @@hostname")
    String getHost(int id);
}
