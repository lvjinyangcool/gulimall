package com.atguigu.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 同一个对象内事务方法互调默认失效, 原因 绕过了代理对象 事务使用代理对象来控制的
 *   解决：使用代理对象来调用事务方法
 *   	1. 引入 spring-boot-starter-aop 它帮我们引入了aspectj
 *    	2. @EnableAspectJAutoProxy(exposeProxy = true) [对外暴露代理对象] 开启动态代理功能 而不是jdk默认的动态代理 即使没有接口也可以创建动态代理
 *  	3. 本类互调用代理对象		AopContext
 *
 *  seata
 *
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableFeignClients
@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableRabbit
@MapperScan("com.atguigu.gulimall.order.dao")
@SpringBootApplication
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
