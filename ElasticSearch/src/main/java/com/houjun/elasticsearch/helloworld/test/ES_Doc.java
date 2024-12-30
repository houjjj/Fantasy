package com.houjun.elasticsearch.helloworld.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.houjun.elasticsearch.helloworld.domain.User;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author HouJun
 * @date 2021-12-05 11:16
 */
public class ES_Doc {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost",9200)));
//        add(restHighLevelClient);
//        modify(restHighLevelClient);
//        get(restHighLevelClient);
//        delete(restHighLevelClient);
//        batchAdd(restHighLevelClient);
//        batchDelete(restHighLevelClient);
//        search_matchAll(restHighLevelClient);
//        search_condition(restHighLevelClient);
        search_multi_condition(restHighLevelClient);
        restHighLevelClient.close();
    }

    /**
     * 组合查询,最好是每次创建BoolQueryBuilder 对象，所以条件在下面添加
     * @param restHighLevelClient
     */
    private static void search_multi_condition(RestHighLevelClient restHighLevelClient) throws IOException {
        SearchRequest searchRequest = new SearchRequest("shopping");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 不使用中间类 QueryBuilders.matchQuery分词搜索，QueryBuilders.termQuery完全匹配搜索
//        sourceBuilder.query(QueryBuilders.termQuery("category.keyword","苹果"));
//        sourceBuilder.query(QueryBuilders.matchQuery("category","苹为"));
//        sourceBuilder.query(QueryBuilders.rangeQuery("price").gt("5000").lt("10000"));

        // 组合条件查询-must、should.filter..
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.must();
        boolQueryBuilder.should(QueryBuilders.termQuery("category.keyword","苹果"));
        boolQueryBuilder.should(QueryBuilders.termQuery("category.keyword","华为"));
//        boolQueryBuilder.must(rangeQueryBuilder);
        sourceBuilder.query(boolQueryBuilder);

        // 范围查询
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
        rangeQueryBuilder.gt("3000");
        rangeQueryBuilder.lt("1000");
        sourceBuilder.query(rangeQueryBuilder);

        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(response);
        System.out.println(Arrays.toString(response.getHits().getHits()));
        System.out.println(response.getTook());
    }
    /**
     * 条件查询-排序-分页-根据条件查询
     * @param restHighLevelClient
     */
    private static void search_condition(RestHighLevelClient restHighLevelClient) throws IOException {
        SearchRequest searchRequest = new SearchRequest("shopping");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 排序
        sourceBuilder.sort("price", SortOrder.DESC);
        // 分页
        sourceBuilder.from(0).size(2);
        // 根据条件查询 QueryBuilders.matchQuery分词搜索，QueryBuilders.termQuery完全匹配搜索
//        sourceBuilder.query(QueryBuilders.termQuery("category.keyword","苹果"));
//        sourceBuilder.query(QueryBuilders.matchQuery("category","苹为"));

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.must();
        boolQueryBuilder.should(QueryBuilders.termQuery("category.keyword","苹果"));
        boolQueryBuilder.should(QueryBuilders.termQuery("category.keyword","华为"));
        sourceBuilder.query(boolQueryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(response);
        System.out.println(Arrays.toString(response.getHits().getHits()));
        System.out.println(response.getTook());
    }

    /**
     * 全量查询
     */
    private static void search_matchAll(RestHighLevelClient restHighLevelClient) throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 查询全部数据
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(response);
        System.out.println(Arrays.toString(response.getHits().getHits()));
        System.out.println(response.getTook());
    }

    /**
     * 批量删除
     */
    private static void batchDelete(RestHighLevelClient restHighLevelClient) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new DeleteRequest("user","1001"));
        bulkRequest.add(new DeleteRequest("user","1002"));
        bulkRequest.add(new DeleteRequest("user","1003"));
        BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    /**
     * 批量添加
     */
    private static void batchAdd(RestHighLevelClient restHighLevelClient) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest().index("user").id("1002").source(XContentType.JSON,"name","小明1","sex","男"));
        bulkRequest.add(new IndexRequest().index("user").id("1003").source(XContentType.JSON,"name","小伟1","sex","男"));
        bulkRequest.add(new IndexRequest().index("user").id("1004").source(XContentType.JSON,"name","小红1","sex","女"));
        BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    /**
     * 文档--查询
     * @param restHighLevelClient
     */
    private static void get(RestHighLevelClient restHighLevelClient) throws IOException {
        GetRequest getRequest = new GetRequest();
        getRequest.index("user").id("1001");
        GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    /**
     * 文档-删除
     * @param restHighLevelClient
     */
    private static void delete(RestHighLevelClient restHighLevelClient) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.index("user").id("1001");
        DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    /**
     * 文档-局部修改
     */
    private static void modify(RestHighLevelClient restHighLevelClient) throws IOException {
        UpdateRequest user = new UpdateRequest();
        user.index("user").id("1001").doc(XContentType.JSON,"sex","女的");

        UpdateResponse response = restHighLevelClient.update(user, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    /**
     * 文档-添加
     */
    private static void add(RestHighLevelClient restHighLevelClient) throws IOException {
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.index("user").id("1001");
        User user = new User();
        user.setName("张三");
        user.setSex("男的");
        user.setTel(1234);
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);
        indexRequest.source(userJson,XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(response);
    }


}
