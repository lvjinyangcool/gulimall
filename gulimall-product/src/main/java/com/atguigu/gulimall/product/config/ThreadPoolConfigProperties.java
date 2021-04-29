package com.atguigu.gulimall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 自定义线程池参数配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "gulimall.thread")
public class ThreadPoolConfigProperties {

    /**
     * 核心线程池大小
     */
    private Integer coreSize;

    /**
     * 最大线程池大小
     */
    private Integer maxSize;

    /**
     * 存活时间
     */
    private Integer keepAliveTime;

    /**
     * 阻塞队列长度
     */
    private Integer blockDequeLength;
}
