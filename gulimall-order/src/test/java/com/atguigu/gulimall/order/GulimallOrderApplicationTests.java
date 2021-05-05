package com.atguigu.gulimall.order;

import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@Slf4j
@SpringBootTest
class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 发送的消息是一个对象 必须实现序列化
     */
    @Test
    public void sendMessageTest(){
        OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
        reasonEntity.setId(1L);
        reasonEntity.setCreateTime(new Date());
        reasonEntity.setName("hehe");

        rabbitTemplate.convertAndSend("hello.java.exchange","hello.java",reasonEntity);

        log.info("消息发送完成{}", reasonEntity);
    }



    /**
     * 1.如何创建Exchange[hello.java.exchange]丶Queue丶Binding
     *      1)丶使用AmqpAdmin进行创建
     * 2.如何收发消息
     *
     */
    @Test
    public void createExchange(){
        //创建Exchange
        //Exchange
        DirectExchange directExchange = new DirectExchange(
                "hello.java.exchange",true, false);
        amqpAdmin.declareExchange(directExchange);
        log.info("exchange[{}]创建成功", "hello.java.exchange");
    }

    @Test
    public void createQueue() {
        // 持久化：true  是否排他：false 是否自动删除：false
        Queue queue = new Queue("hello.java.queue", true, false, false);
        amqpAdmin.declareQueue(queue);
        log.info("\nQueue [" + queue.getName() + "] 创建成功");
    }

    @Test
    public void bindIng() {
        //String destination  目的地
        //DestinationType destinationType  目的地类型
        //String exchange  交换机
        //String routingKey 路由键
        //Map<String, Object> arguments  自定义参数
        Binding binding = new Binding("hello.java.queue", Binding.DestinationType.QUEUE, "hello.java.exchange", "hello.java", null);
        amqpAdmin.declareBinding(binding);
        log.info("\n[" + binding.getExchange() + "] 与 [" + binding.getDestination() + "] 绑定成功");
    }

}
