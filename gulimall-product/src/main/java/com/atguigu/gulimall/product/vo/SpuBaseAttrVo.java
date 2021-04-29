package com.atguigu.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SpuBaseAttrVo {

    /**
     * 属性名
     */
    private String attrName;
    /**
     * 属性值
     */
    private String attrValue;
}
