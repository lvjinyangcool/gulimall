package com.atguigu.gulimall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyTreadConfig {

    @Bean
    public ThreadPoolExecutor getThreadPoolExecutor(ThreadPoolConfigProperties configProperties){
        return new ThreadPoolExecutor(configProperties.getCoreSize(),
                configProperties.getMaxSize(),
                configProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(configProperties.getBlockDequeLength()),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
                );
    }
}
