package com.atguigu.gulimall.order.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单确认页需要用的数据
 */
@ToString
public class OrderConfirmVo {

    /**
     * 收获地址
     */
    @Setter @Getter
    List<MemberAddressVo> address;

    /**
     * 所有选中的购物项
     */
    @Setter @Getter
    List<OrderItemVo> items;

    //发票记录。。。。

    //优惠券心......

    /**
     * 积分信息
     */
    @Setter @Getter
    private Integer integration;

    /**
     * 商品库存
     */
    @Setter @Getter
    Map<Long,Boolean> stocks;

    /**
     * 订单总额
     */
//    BigDecimal totalPrice;

    /**
     * 应付金额
     */
//    BigDecimal payPrice;

    /**
     * 防重令牌
     */
    @Setter @Getter
    private String orderToken;

    public BigDecimal getTotal(){
        BigDecimal sum = new BigDecimal("0");
        if(!CollectionUtils.isEmpty(items)){
            for (OrderItemVo item : items) {
                sum = sum.add(item.getPrice().multiply(new BigDecimal(item.getCount().toString())));
            }
        }
        return sum;
    }

    public BigDecimal getPayPrice(){
        return this.getTotal();
    }

    public Integer getCount(){
        Integer total = 0;
        if(!CollectionUtils.isEmpty(items)){
            total = items.stream().map(OrderItemVo::getCount).reduce(Integer::sum).orElse(0);
        }
        return total;
    }

}
