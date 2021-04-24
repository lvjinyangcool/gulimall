package com.atguigu.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRedissonConfig {


    /**
     * 所有对redisson的使用都是通过RedissonClient对象
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        //1.
        Config config = new Config();
        // 创建单例模式的配置
        config.useSingleServer().setAddress("redis://"+"192.168.56.10:6379");
        return Redisson.create(config);
    }
}
