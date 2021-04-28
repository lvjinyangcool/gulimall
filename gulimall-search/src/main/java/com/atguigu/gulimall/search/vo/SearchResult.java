package com.atguigu.gulimall.search.vo;

import com.atguigu.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 包含页面需要的所有信息
 */
@Data
public class SearchResult {

    /**
     * 查询到的所有商品信息
     */
    private List<SkuEsModel> products;



    //***************** 以下是分页信息  ***********************//

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页码
     */
    private Long totalPages;

    /**
     * 当前查询到的结果,所有涉及到的品牌
     */
    private List<BrandVO> brandVOS;

    /**
     * 当前查询的结果 所有涉及到所有分类
     */
    private List<CatalogVO> CatalogVOS;

    /**
     * 当前查询的结果 所有涉及到所有属性
     */
    private List<AttrVO> attrVOS;

    /**
     * 导航页
     */
    private List<Integer> pageNavs;

    //	================以上是返回给页面的所有信息================

    // 面包屑导航数据
    private  List<NavVo> navs = new ArrayList<>();


    /**
     * 便于判断当前id是否被使用
     */
    private List<Long> attrIds = new ArrayList<>();


    @Data
    public static class NavVo{
        private String name;

        private String navValue;

        private String link;
    }

    @Data
    public static class BrandVO{
        /**
         * 品牌id
         */
        private Long brandId;

        /**
         * 品牌名称
         */
        private String brandName;

        /**
         * 品牌图片
         */
        private String brandImg;
    }


    @Data
    public static class CatalogVO{
        /**
         * 分类id
         */
        private Long catalogId;

        /**
         * 分类名称
         */
        private String catalogName;
    }

    @Data
    public static class AttrVO{
        /**
         * 属性id
         */
        private Long attrId;

        /**
         * 属性名称
         */
        private String attrName;

        /**
         * 属性值
         */
        private List<String> attrValue;
    }
}
