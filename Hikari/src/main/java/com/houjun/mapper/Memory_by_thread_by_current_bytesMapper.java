package com.houjun.mapper;

import com.houjun.domain.Memory_by_thread_by_current_bytes;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Select;

import java.util.List;
//@Mapper
//@CacheNamespace
public interface Memory_by_thread_by_current_bytesMapper {

//    @Select("select SQL_NO_CACHE  thread_id,user,current_allocated from sys.memory_by_thread_by_current_bytes")
    List<Memory_by_thread_by_current_bytes> query();
}
