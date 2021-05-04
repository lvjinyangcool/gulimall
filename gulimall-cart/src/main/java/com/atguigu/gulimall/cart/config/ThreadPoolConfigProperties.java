package com.atguigu.gulimall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>Title: ThreadPoolConfigProperties</p>
 * Description：
 * date：2020/6/28 11:39
 */
@ConfigurationProperties(prefix = "gulimall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {

	private Integer coreSize;

	private Integer maxSize;

	private Integer keepAliveTime;
}
