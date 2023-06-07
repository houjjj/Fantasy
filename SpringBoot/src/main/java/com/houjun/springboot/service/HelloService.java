package com.houjun.springboot.service;

import com.houjun.springboot.domain.CloudApp;
import com.houjun.springboot.mapper.HelloMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author: houjun
 * @Date: 2023/5/5 - 15:45
 * @Description:
 */
@Service
@Slf4j
public class HelloService {

    @Autowired
    private HelloMapper mapper;

    public void update() {
        while (true) {
            CloudApp app = new CloudApp();
            app.setId(3235);
            app.setUpdateTime(LocalDateTime.now());
            mapper.update(app);
            log.info("[update] 更新...");
        }
    }
    public void insert() {
        while (true) {
            CloudApp app = new CloudApp();
            app.setName(LocalDateTime.now().toString());
            app.setCreateTime(LocalDateTime.now());
            app.setKubeId(1);
            app.setNamespace("could");
            app.setStatus("deleted");
            app.setDeleted(false);
            mapper.insert(app);
            log.info("[insert] 插入...");
        }
    }
}
