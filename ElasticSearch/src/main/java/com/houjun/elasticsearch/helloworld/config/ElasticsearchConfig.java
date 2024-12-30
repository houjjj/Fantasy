package com.houjun.elasticsearch.helloworld.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class ElasticsearchConfig {


    @Value("${es.ip}")
    private String esIP;

    @Value("${es.port}")
    private int esPort;

    @Value("${es.username}")
    private String username;

    @Value("${es.password}")
    private String password;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestClientBuilder builder = RestClient.builder(
                new HttpHost(esIP, esPort, "http")  // Elasticsearch 地址和端口
        );
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        // 配置超时和连接数
        builder.setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder
                        .setConnectTimeout(5000)  // 连接超时时间（毫秒）
                        .setSocketTimeout(30000)  // 数据传输超时时间（毫秒）
                        .setConnectionRequestTimeout(1000));  // 连接请求超时

        // 方法1，更容易理解
        builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                // 配置连接池
                return httpClientBuilder
                        .setDefaultCredentialsProvider(basicCredentialsProvider)
                        .setMaxConnTotal(100)  // 最大连接数
                        .setMaxConnPerRoute(20); // 每个路由的最大连接数
            }
        });
        // 方法2，代码量少
        /*builder.setHttpClientConfigCallback(httpClientBuilder -> {
            // 配置连接池
            return httpClientBuilder
                    .setDefaultCredentialsProvider(basicCredentialsProvider)
                    .setMaxConnTotal(100)  // 最大连接数
                    .setMaxConnPerRoute(20); // 每个路由的最大连接数
         });*/

        return new RestHighLevelClient(builder);
    }
}
