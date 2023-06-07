//package com.houjun.redission;
//
//import org.redisson.Redisson;
//import org.redisson.api.RedissonClient;
//import org.redisson.config.ClusterServersConfig;
//import org.redisson.config.Config;
//import org.redisson.config.SentinelServersConfig;
//import org.redisson.config.SingleServerConfig;
//import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.ReflectionUtils;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * @Author: houjun
// * @Date: 2023/5/4 - 14:38
// * @Description:
// */
//@Configuration
//public class RedissonConfig {
//    @Autowired
//    private RedisProperties redisProperties;
//
//    @Bean("redissonClient")
//    public RedissonClient redissonClient() {
//        Method clusterMethod = ReflectionUtils.findMethod(RedisProperties.class, "getCluster");
//        Method usernameMethod = ReflectionUtils.findMethod(RedisProperties.class, "getUsername");
//        Method timeoutMethod = ReflectionUtils.findMethod(RedisProperties.class, "getTimeout");
//        Object timeoutValue = ReflectionUtils.invokeMethod(timeoutMethod, this.redisProperties);
//        int timeout;
//        if (null == timeoutValue) {
//            timeout = 10000;
//        } else if (!(timeoutValue instanceof Integer)) {
//            Method millisMethod = ReflectionUtils.findMethod(timeoutValue.getClass(), "toMillis");
//            timeout = ((Long)ReflectionUtils.invokeMethod(millisMethod, timeoutValue)).intValue();
//        } else {
//            timeout = (Integer)timeoutValue;
//        }
//
//        String username = null;
//        if (usernameMethod != null) {
//            username = (String)ReflectionUtils.invokeMethod(usernameMethod, this.redisProperties);
//        }
//
//        Config config;
//        if (this.redissonProperties.getConfig() != null) {
//            try {
//                config = Config.fromYAML(this.redissonProperties.getConfig());
//            } catch (IOException var15) {
//                try {
//                    config = Config.fromJSON(this.redissonProperties.getConfig());
//                } catch (IOException var14) {
//                    var14.addSuppressed(var15);
//                    throw new IllegalArgumentException("Can't parse config", var14);
//                }
//            }
//        } else if (this.redissonProperties.getFile() != null) {
//            try {
//                InputStream is = this.getConfigStream();
//                config = Config.fromYAML(is);
//            } catch (IOException var13) {
//                try {
//                    InputStream is = this.getConfigStream();
//                    config = Config.fromJSON(is);
//                } catch (IOException var12) {
//                    var12.addSuppressed(var13);
//                    throw new IllegalArgumentException("Can't parse config", var12);
//                }
//            }
//        } else if (this.redisProperties.getSentinel() != null) {
//            Method nodesMethod = ReflectionUtils.findMethod(RedisProperties.Sentinel.class, "getNodes");
//            Object nodesValue = ReflectionUtils.invokeMethod(nodesMethod, this.redisProperties.getSentinel());
//            String[] nodes;
//            if (nodesValue instanceof String) {
//                nodes = this.convert(Arrays.asList(((String)nodesValue).split(",")));
//            } else {
//                nodes = this.convert((List)nodesValue);
//            }
//
//            config = new Config();
//            ((SentinelServersConfig)((SentinelServersConfig)config.useSentinelServers().setMasterName(this.redisProperties.getSentinel().getMaster()).addSentinelAddress(nodes).setDatabase(this.redisProperties.getDatabase()).setConnectTimeout(timeout)).setUsername(username)).setPassword(this.redisProperties.getPassword())
//            .setSentinelsDiscovery(fa);
//        } else {
//            Method method;
//            if (clusterMethod != null && ReflectionUtils.invokeMethod(clusterMethod, this.redisProperties) != null) {
//                Object clusterObject = ReflectionUtils.invokeMethod(clusterMethod, this.redisProperties);
//                method = ReflectionUtils.findMethod(clusterObject.getClass(), "getNodes");
//                List<String> nodesObject = (List)ReflectionUtils.invokeMethod(method, clusterObject);
//                String[] nodes = this.convert(nodesObject);
//                config = new Config();
//                ((ClusterServersConfig)((ClusterServersConfig)config.useClusterServers().addNodeAddress(nodes).setConnectTimeout(timeout)).setUsername(username)).setPassword(this.redisProperties.getPassword());
//            } else {
//                config = new Config();
//                String prefix = "redis://";
//                method = ReflectionUtils.findMethod(RedisProperties.class, "isSsl");
//                if (method != null && (Boolean)ReflectionUtils.invokeMethod(method, this.redisProperties)) {
//                    prefix = "rediss://";
//                }
//
//                ((SingleServerConfig)((SingleServerConfig)config.useSingleServer().setAddress(prefix + this.redisProperties.getHost() + ":" + this.redisProperties.getPort()).setConnectTimeout(timeout)).setDatabase(this.redisProperties.getDatabase()).setUsername(username)).setPassword(this.redisProperties.getPassword());
//            }
//        }
//
//        if (this.redissonAutoConfigurationCustomizers != null) {
//            Iterator var22 = this.redissonAutoConfigurationCustomizers.iterator();
//
//            while(var22.hasNext()) {
//                RedissonAutoConfigurationCustomizer customizer = (RedissonAutoConfigurationCustomizer)var22.next();
//                customizer.customize(config);
//            }
//        }
//        config.useSentinelServers().setMasterName(this.redisProperties.getSentinel().getMaster()).addSentinelAddress(nodes).setDatabase(this.redisProperties.getDatabase()).setConnectTimeout(timeout).setUsername(username).setPassword(this.redisProperties.getPassword()).setCheckSentinelsList(false);
//        config.useSentinelServers().setSentinelsDiscovery(false);
//        return Redisson.create(config);
//    }
//
//    private String[] convert(List<String> nodesObject) {
//        List<String> nodes = new ArrayList<String>(nodesObject.size());
//        for (String node : nodesObject) {
//            if (!node.startsWith("redis://") && !node.startsWith("rediss://")) {
//                nodes.add("redis://" + node);
//            } else {
//                nodes.add(node);
//            }
//        }
//        return nodes.toArray(new String[nodes.size()]);
//    }
//}
