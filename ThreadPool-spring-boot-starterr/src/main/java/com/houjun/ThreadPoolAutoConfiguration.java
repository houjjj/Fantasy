package com.houjun;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolAutoConfiguration {

    @Bean
    @ConditionalOnClass(ThreadPoolExecutor.class)
    public ThreadPoolExecutor myThreadPool() {
        return new ThreadPoolExecutor(10, 20, 10, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1024), new NamedThreadFactory("thread pool starter"), new ThreadPoolExecutor.DiscardOldestPolicy());
    }
}
