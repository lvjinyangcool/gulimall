package com.atguigu.gulimall.cart.vo;

import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 整个购物车
 */

public class Cart {

    /**
     * 购物项列表
     */
    private List<CartItem> items;

    /**
     * 商品数量
     */
    private Integer countNum;

    /**
     * 商品类型数量
     */
    private Integer countType;

    /**
     * 商品总价
     */
    private BigDecimal totalAmount;

    /**
     * 减免价格
     */
    private BigDecimal reduce = new BigDecimal("0.00");

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        return this.items.stream().map(CartItem::getCount).reduce(Integer::sum).orElse(0);
    }

    public Integer getCountType() {
        return CollectionUtils.isEmpty(this.items) ? 0 : this.items.size();
    }

    public BigDecimal getTotalAmount() {

        return this.items.stream().filter(CartItem::getCheck)
                .map(CartItem::getTotalPrice).reduce(BigDecimal::add)
                .orElse(new BigDecimal("0"));
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
