package com.houjun.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.List;

public class JedisPipelineExample {
    public static void main(String[] args) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {

            Pipeline pipeline = jedis.pipelined();

            // 批量设置
            for (int i = 0; i < 10; i++) {
                pipeline.set("key" + i, "value" + i);
            }

            // 批量获取
            for (int i = 0; i < 10; i++) {
                pipeline.get("key" + i);
            }

            // 同步执行所有命令并返回结果
            List<Object> results = pipeline.syncAndReturnAll();

            results.forEach(System.out::println);
        }
    }
}
