package com.houjun.springboot.service;

import com.houjun.springboot.domain.CloudApp;
import com.houjun.springboot.mapper.HelloMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        CloudApp app = new CloudApp();
        app.setId(1401);
        app.setName("3235");
        app.setUpdateTime(LocalDateTime.now());
        mapper.update(app);
        log.info("[update] 更新...");

    }

    public void insert() {
        for (int i = 0; i < 10; i++) {
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

    @Transactional(readOnly = true)
    public CloudApp get(int id) {
        CloudApp app = mapper.get(id);
        String host = mapper.getHost(id);
        System.out.println(host);
        return app;
    }
}
