package com.houjun.elasticsearch.helloworld.service;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ElasticsearchService {

    @Autowired
    private RestHighLevelClient client;

    @Scheduled(cron = "*/1 * * * * *")
    public void testConnection() {
        try {
            Response response = client.getLowLevelClient()
                    .performRequest(new Request("GET", "/"));
            Date date = new Date();
            System.out.printf("时间%s,Elasticsearch连接成功，状态：%s \n",date,   response.getStatusLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
