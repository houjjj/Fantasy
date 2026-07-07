package com.houjun.elasticsearch.helloworld.controller;

import com.houjun.elasticsearch.helloworld.domain.ESArticle;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/v2")
public class ESV2Controller {
    //直接引入即可，无需额外的 Bean 配置和序列化配置
    @Resource
    private ElasticsearchRestTemplate elasticTemplate;


    @RequestMapping("/createIndex")
    public Boolean createIndex(String indexName) {
        ESArticle esArticle = new ESArticle();
        esArticle.setTitle("testtest");
        elasticTemplate.save(esArticle);
        return true;
    }
}
