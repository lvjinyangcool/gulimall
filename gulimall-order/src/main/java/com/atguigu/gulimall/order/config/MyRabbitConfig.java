package com.atguigu.gulimall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;


    /**
     * 定制RabbitTemplate
     * 1丶服务收到消息进行回调
     *      1.spring.rabbitmq.publisher-confirm-type = correlated
     *      2.设置确认回调 ConfirmCallback
     * 2丶消息正确抵达队列进行回调
     *      1.spring.rabbitmq.publisher-returns = true
     *      2.spring.rabbitmq.template.mandatory = true
     *      3.设置投递队列失败的确认回调 ReturnCallback
     * 3.消费端确认(确认每个消息被正确消息，此时Broker才可删除消息)
     *      1.默认是自动确认的，只要消息接收到，客户端会自动确认，服务端接收ack删除消息
     *          问题:
     *              我们收到很多消息，自动回复给服务器ack，只有一个消息处理成功,此时宕机，就会丢失消息
     *
     *              消费者手动确认模式。只要我们没有明确告诉MQ，货物没签收。没有ack，消息就一直是unacked状态。
     *              即使Consumer宕机，
     *		如何签收:
     *			签收: channel.basicAck(deliveryTag, false);
     *			拒签: channel.basicNack(deliveryTag, false,true);
     *	 配置文件中一定要加上这个配置
     *		listener:
     *       simple:
     *         acknowledge-mode: manual
     */
    @PostConstruct //MyRabbitConfig对象创建完成以后，执行这个方法
    public void initRabbitTemplate(){
        //设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *  只要消息抵达Broker，就ack=true
             *
             * @param correlationData  当前消息的唯一关联数据 (这个是消息的唯一id)
             * @param ack     消息是否成功收到
             * @param cause     失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("confirm...... correlationData=[" + correlationData + "]\n"+"ack=["+ack+"]\n" + "cause=["+cause+"]");
            }
        });

        //设置消息未抵达队列的失败确认回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 设置消息抵达队列回调：可以很明确的知道那些消息失败了
             * 消息没有投递给指定的队列，就会触发这个失败回调
             *
             * @param message       投递失败的消息详细信息
             * @param replyCode     回复的状态码
             * @param replyText     回复的文本内容
             * @param exchange      当时这个发送给那个交换机
             * @param routingKey    当时这个消息用那个路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println();
            }
        });


    }



    /**
     * 使用JSON序列化机制，进行消息转换
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
