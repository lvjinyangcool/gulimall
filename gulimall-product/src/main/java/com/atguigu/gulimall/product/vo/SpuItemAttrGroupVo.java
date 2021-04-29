package com.atguigu.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 分组的spu信息
 */
@Data
@ToString
public class SpuItemAttrGroupVo {

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 每个分组下的属性列表
     */
    private List<SpuBaseAttrVo> attrs;

}
