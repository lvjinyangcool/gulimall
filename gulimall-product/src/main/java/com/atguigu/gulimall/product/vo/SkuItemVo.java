package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * 商品详情信息
 */
@Data
public class SkuItemVo {

    //1.sku基本信息获取  pms_sku_info
    SkuInfoEntity info;

    boolean hasStock = true;

    //2.sku的图片信息  pms_sku_images
    List<SkuImagesEntity> images;

    //3.获取spu的销售属性组合。
    List<SkuItemSaleAttrVo> saleAttr;

    //4.获取sku的介绍
    SpuInfoDescEntity desc;

    //5.获取spu的规格参数信息。
    List<SpuItemAttrGroupVo> groupAttrs;

    /**
     * 秒杀信息
     */
    SeckillInfoVo seckillInfoVo;
}
