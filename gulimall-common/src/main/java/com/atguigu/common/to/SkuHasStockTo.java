package com.atguigu.common.to;

import lombok.Data;

/**
 * 存储这个sku是否有库存
 */
@Data
public class SkuHasStockTo {

    private Long skuId;

    private Boolean hasStock;
}
