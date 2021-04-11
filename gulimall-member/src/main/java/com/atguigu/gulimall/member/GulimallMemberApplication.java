package com.atguigu.gulimall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

/**
 * 1。想要远程调用别的服务
 *  1)丶引入open-feign
 *  2)丶编写一个接口，告诉springcloud这个接口需要调用远程服务
 *
 */
@EnableFeignClients(basePackages="com.atguigu.gulimall.member.feign")//扫描接口方法注解
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.atguigu.gulimall.member.dao")
public class GulimallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallMemberApplication.class, args);
    }

}
