package com.atguigu.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 1.整合mybatis-plus
 *     1)丶导入依赖
 *      <dependency>
 *             <groupId>com.baomidou</groupId>
 *             <artifactId>mybatis-plus-boot-starter</artifactId>
 *             <version>3.3.2</version>
 *         </dependency>
 *     2)丶配置
 *          1.配置数据源
 *              1)丶导入数据库的驱动
 *              2)丶在application.yml配置数据源相关信息
 *          2.配置Mybatis-plus
 *              1)丶使用@MapperScan
 *              2)丶告诉mybatis-plus，sql映射文件位置
 *
 *
 */
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
