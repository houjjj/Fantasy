package com.houjun.lecttuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisStringCommands;

import java.util.concurrent.ExecutionException;

/**
 * @Author: houjun
 * @Date: 2022/8/23 - 23:33
 * @Description:
 */
public class HelloWorld {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        RedisClient client = RedisClient.create("redis://192.168.20.106");
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisStringCommands sync = connection.sync();
        String value = (String) sync.get("mama");
        System.out.println(value);
        RedisAsyncCommands<String, String> async = connection.async();
        RedisFuture<String> set = async.set("key", "value");
        RedisFuture<String> get = async.get("key");
        System.out.println(set.get());
        System.out.println(get.get());

    }
}
