package com.houjun.service;

import com.houjun.domain.Memory_by_thread_by_current_bytes;
import com.houjun.mapper.Memory_by_thread_by_current_bytesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Memory_by_thread_by_current_bytesService {

    @Autowired
    Memory_by_thread_by_current_bytesMapper memoryByThreadByCurrentBytesMapper;

    public List<Memory_by_thread_by_current_bytes> getMemoryByThreadByCurrentBytes() {
        return memoryByThreadByCurrentBytesMapper.query();
    }
}
