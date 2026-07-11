package com.houjun.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Configuration
@ConditionalOnProperty(name = "spring.redis.sentinel.nodes")
public class JedisSentinelConfig {

    // 1. 基础连接配置
    @Value("${spring.redis.sentinel.master}")
    private String masterName;

    @Value("${spring.redis.sentinel.nodes}")
    private String nodes;

    @Value("${spring.redis.password}")
    private String password;

    // 2. 线程池配置
    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private long maxWait;

    // 验活属性
    @Value("${spring.redis.jedis.pool.custom.test-on-borrow:true}")
    private boolean testOnBorrow;

    @Value("${spring.redis.jedis.pool.custom.test-while-idle:true}")
    private boolean testWhileIdle;

    @Value("${spring.redis.jedis.pool.custom.time-between-eviction-runs-millis:30000}")
    private long timeBetweenEvictionRunsMillis;


    // 第一步：创建并配置线程池本身
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWait);

        // 关键： 高级配置
        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setTestWhileIdle(testWhileIdle);
        poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        return poolConfig;
    }

    // 第二步：将 poolConfig 注入到 JedisConnectionFactory 中
    @Bean
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {

        // 2.1 针对 “哨兵模式（sentinel）”进行配置

        RedisSentinelConfiguration redisConfig = new RedisSentinelConfiguration();
        // 1. 设置主节点名称 (Master)
        redisConfig.setMaster(masterName);
        // 2. 设置密码
        redisConfig.setPassword(password);

        // 3. 解析多哨兵节点 (支持 host:port,host:port 格式)
        Set<RedisNode> sentinelNodes = new HashSet<>();
        for (String node : nodes.split(",")) {
            String[] parts = node.trim().split(":");
            if (parts.length == 2) {
                sentinelNodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
            }
        }
        redisConfig.setSentinels(sentinelNodes);


        // 2.2 核心步骤：通过 ClientConfiguration 将【线程池】包装进去
        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder()
                .usePooling() // 开启线程池支持
                .poolConfig(jedisPoolConfig) //    这里就是“注入”：把上面定义好的 poolConfig 传进来
                .and()
                .readTimeout(Duration.ofMillis(5000))
                .build();

        // 2.3 最终组装成 ConnectionFactory
        return new JedisConnectionFactory(redisConfig, jedisClientConfiguration);
    }

//    // 第三步：配置常用的 RedisTemplate
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        // 将包含了自定义线程池的工厂设置给 Template
//        redisTemplate.setConnectionFactory(jedisConnectionFactory);
//
//
//
//        //使用jackson进行序列化
//        Jackson2JsonRedisSerializer jsonRedisSerializer =
//                new Jackson2JsonRedisSerializer(Object.class);
//        //规定序列化规则
//        ObjectMapper objectMapper = new ObjectMapper();
//        /**
//         * 第一个参数指的是序列化的域，ALL指的是字段、get和set方法、构造方法
//         * 第二个参数指的是序列化哪些访问修饰符，默认是public，ANY指任何访问修饰符
//         */
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        //指定序列化输入的类型，类必须是非final修饰的类
//        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jsonRedisSerializer.setObjectMapper(objectMapper);
//        //序列化key value
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
//        redisTemplate.afterPropertiesSet();
//        return redisTemplate;
//    }


}