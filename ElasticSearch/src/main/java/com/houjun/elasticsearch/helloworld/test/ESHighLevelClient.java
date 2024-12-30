package com.houjun.elasticsearch.helloworld.test;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;

import java.io.IOException;

/**
 * @author HouJun
 * @date 2021-12-05 11:16
 */
public class ESHighLevelClient {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost",9200)));
        creat(restHighLevelClient);
//        delete(restHighLevelClient);
        get(restHighLevelClient);
        restHighLevelClient.close();
    }

    private static void get(RestHighLevelClient restHighLevelClient) throws IOException {
        GetIndexResponse user = restHighLevelClient.indices().get(new GetIndexRequest("user"), RequestOptions.DEFAULT);
        System.out.println(user.getAliases());
        System.out.println(user.getDataStreams());
        System.out.println(user.getMappings());
        System.out.println(user.getSettings());
    }

    private static void delete(RestHighLevelClient restHighLevelClient) throws IOException {
        AcknowledgedResponse user = restHighLevelClient.indices().delete(new DeleteIndexRequest("user"), RequestOptions.DEFAULT);
        System.out.println(user.isAcknowledged());
    }

    private static void creat(RestHighLevelClient restHighLevelClient) throws IOException {
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(new CreateIndexRequest("user"), RequestOptions.DEFAULT);
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println("创建索引结果：" + acknowledged);
    }


}
