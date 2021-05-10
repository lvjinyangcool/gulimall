package com.atguigu.gulimall.ware.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 库存锁定结果
 */
@Data
@ToString
public class LockStockResult {

    /**
     * 商品skuId
     */
    private Long skuId;

    /**
     * 锁定数量
     */
    private Integer num;

    /**
     * 是否锁定成功
     */
    private Boolean locked;
}
