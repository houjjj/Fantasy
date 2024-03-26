package com.houjun.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Author: houjun
 * @Date: 2022/8/23 - 23:33
 * @Description:
 */
public class HelloWorld {
    public static void main(String[] args) {
        JedisPool pool = new JedisPool("192.168.20.106", 6379);
//        JedisPool pool = new JedisPool( );
        try (Jedis resource = pool.getResource()) {
//            resource.set("notion","china");
            System.out.println(resource.get("mama"));
            System.out.println(resource.get("notion"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
