package com.atguigu.common.to.es;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>Title: SkuEsModel</p>
 * Description：
 * "mappings": {
 *     "properties": {
 *       "skuId":{
 *         "type": "long"
 *       },
 *       "spuId":{
 *         "type": "keyword"
 *       },
 *       "skuTitle":{
 *         "type": "text",
 *         "analyzer": "ik_smart"
 *       },
 *       "skuPrice":{
 *         "type": "keyword"
 *       },
 *       "skuImg":{
 *         "type": "keyword",
 *         "index": false,
 *         "doc_values": false
 *       },
 *       "saleCount":{
 *         "type": "long"
 *       },
 *       "hasStock":{
 *         "type": "boolean"
 *       },
 *       "hotScore":{
 *         "type": "long"
 *       },
 *       "brandId":{
 *         "type": "long"
 *       },
 *       "catalogId":{
 *         "type": "long"
 *       },
 *       "brandName":{
 *         "type":"keyword",
 *         "index": false,
 *         "doc_values": false
 *       },
 *       "brandImg":{
 *         "type": "keyword",
 *         "index": false,
 *         "doc_values": false
 *       },
 *       "catalogName":{
 *         "type": "keyword",
 *         "index": false,
 *         "doc_values": false
 *       },
 *       "attrs":{
 *         "type": "nested",
 *         "properties": {
 *           "attrId":{
 *             "type":"long"
 *           },
 *           "attrName":{
 *             "type":"keyword",
 *             "index":false,
 *             "doc_values": false
 *           },
 *           "attrValue":{
 *             "type":"keyword"
 *           }
 *         }
 *       }
 *     }
 *   }
 * date：2020/6/8 18:52
 */
@Data
public class SkuEsModel implements Serializable{

    private static final long serialVersionUID = -4528192409765525855L;

    /**
     * skuId
     */
    private Long skuId;

    /**
     * spuId
     */
    private Long spuId;

    /**
     * sku标题
     */
    private String skuTitle;

    /**
     * sku价格
     */
    private BigDecimal skuPrice;

    /**
     * sku图片路径
     */
    private String skuImage;

    /**
     * sku销量
     */
    private Long saleCount;

    /**
     * 是否有库存
     */
    private Boolean hasStock;

    /**
     * 热度评分
     */
    private Long hotScore;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 分类ID
     */
    private Long catalogId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 品牌图片
     */
    private String brandImg;

    /**
     * 分类名称
     */
    private String catalogName;

    /**
     * 检索属性列表
     */
    private List<Attrs> attrs;

    /**
     *  检索属性
     */
    @Data
    public static class Attrs implements Serializable {

        private static final long serialVersionUID = -5841876258930981007L;

        private Long attrId;

        private String attrName;

        private String attrValue;
    }

}
