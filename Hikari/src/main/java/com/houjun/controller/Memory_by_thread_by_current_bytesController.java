package com.houjun.controller;

import com.houjun.domain.Memory_by_thread_by_current_bytes;
import com.houjun.service.Memory_by_thread_by_current_bytesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Memory_by_thread_by_current_bytesController {



    @Autowired
    Memory_by_thread_by_current_bytesService memoryByService;
    @GetMapping("/memList")
    public List<Memory_by_thread_by_current_bytes> memList(){
        return memoryByService.getMemoryByThreadByCurrentBytes();
    }

}
