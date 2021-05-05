package com.atguigu.gulimall.order.controller;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * 测试mq
 */
@RestController
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMQ")
    public String sendMQ(@RequestParam(value = "num", required = false, defaultValue = "10") Integer num){
        OrderEntity entity = new OrderEntity();
        entity.setId(1L);
        entity.setCommentTime(new Date());
        entity.setCreateTime(new Date());
        entity.setConfirmStatus(0);
        entity.setAutoConfirmDay(1);
        entity.setGrowth(1);
        entity.setMemberId(12L);

        OrderItemEntity orderEntity = new OrderItemEntity();
        orderEntity.setCategoryId(225L);
        orderEntity.setId(1L);
        orderEntity.setOrderSn("mall");
        orderEntity.setSpuName("华为");

        for (int i = 0; i < num; i++) {
            if(i % 2 == 0){
                rabbitTemplate.convertAndSend("hello.java.exchange","hello.java",entity, new CorrelationData(UUID.randomUUID().toString()));
            }else {
                rabbitTemplate.convertAndSend("hello.java.exchange","hello.java", orderEntity, new CorrelationData(UUID.randomUUID().toString()));
            }
        }
        return "ok";
    }
}
