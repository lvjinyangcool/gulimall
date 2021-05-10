package com.atguigu.gulimall.order.vo;

import com.atguigu.gulimall.order.entity.OrderEntity;
import lombok.Data;
import lombok.ToString;

/**
 * 创建订单响应数据
 */
@Data
@ToString
public class SubmitOrderResponseVo {

    /**
     * 创建的订单
     */
    private OrderEntity order;

    /**
     * 响应状态码 0:成功  其他情况
     */
    private Integer code;
}
