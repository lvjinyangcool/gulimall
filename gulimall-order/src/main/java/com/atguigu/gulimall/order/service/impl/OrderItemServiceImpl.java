package com.atguigu.gulimall.order.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.order.dao.OrderItemDao;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import com.atguigu.gulimall.order.service.OrderItemService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;


@Service("orderItemService")
@RabbitListener(queues = {"hello.java.queue"})
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }


    /**
     * queues: 声明需要监听的所有队列
     *
     * org.springframework.amqp.core.Message
     *
     * 参数可以写以下类型
     * 1丶Message message: 原生消息详细信息。头+体
     * 2.T<发送的消息的类型> OrderReturnReasonEntity entity
     * 3. Channel channel  当前传输数据的通道
     *
     * Queue：可以有很多人都来监听，只要收到消息。队列删除消息。而且只能有一个收到此消息
     *
     * 场景:
     *    1)丶订单服务启动多个；同一个消息，只能有一个客户端收到
     *    2)丶只要一个消息完成处理完，方法运行结束，才可以接收下一个消息
     *
     *  * 	@RabbitListener： 只能标注在类、方法上 [监听哪些队列]   配合 @RabbitHandler
     * 	*  	@RabbitHandler: 只能标注在方法上	[重载区分不同的消息]
     */
//    @RabbitListener(queues = {"hello.java.queue"})
    @RabbitHandler
    public void receiveMessageA(Message message, OrderReturnReasonEntity entity,
                               Channel channel){
        System.out.println("[receiveMessageA]接受到消息: " + message + "\n内容：" + entity);
    }

    @RabbitHandler
    public void receiveMessageB(Message message, OrderEntity entity,
                               Channel channel){
        System.out.println("[receiveMessageB]接受到消息: " + message + "\n内容：" + entity);
        // 这个是一个数字 通道内自增
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println("deliveryTag==>" + deliveryTag);

        try {
            //签收货物，非批量模式
            channel.basicAck(deliveryTag, false);

            // deliveryTag: 货物的标签  	multiple: 是否批量拒收 requeue: 是否重新入队
//			channel.basicNack(deliveryTag, false,true);

            //批量拒绝
//			channel.basicReject();
        } catch (IOException e) {
            System.out.println("网络中断");
        }

        System.out.println(entity.getOrderSn() + " 消息处理完成");
    }

    @RabbitHandler
    public void receiveMessageC(Message message, OrderItemEntity entity,
                                Channel channel){
        System.out.println("[receiveMessageC]接受到消息: " + message + "\n内容：" + entity);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            //签收货物，非批量模式
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            System.out.println("网络中断");
        }

        System.out.println(entity.getOrderSn() + " 消息处理完成");
    }
}