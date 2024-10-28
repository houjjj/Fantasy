package com.houjun.service;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * @Author: yipeng.liu
 * @Date: 2022/3/2 18:20
 * @Description: RedisListener
 */
@Slf4j
@Configuration
public class RedisListener implements MessageListener {

  @Override
  public void onMessage(Message message, byte[] bytes) {
	//获取订阅消息内容	
    String topic = new String(bytes);
    String context = new String(message.getBody());
    log.info("topic:{},context:{}",topic,context);
  }

  @Bean
  RedisMessageListenerContainer redisMessageListenerContainer(
          RedisConnectionFactory redisConnectionFactory, RedisListener redisListener) {
    RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
    redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
    //订阅topic - subscribe
    redisMessageListenerContainer.addMessageListener(redisListener,new ChannelTopic("cache:redis:caffeine:topic"));
    return redisMessageListenerContainer;
  }

}
