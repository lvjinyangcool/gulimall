package com.atguigu.gulimall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物项内容
 */
public class CartItem {

    /**
     * 商品skuId
     */
    private Long skuId;

    /**
     * 是否选中，默认选中
     */
    private Boolean check = true;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 展示图片
     */
    private String image;

    /**
     * 销售属性
     */
    private List<String> skuAttr;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 购买数量
     */
    private Integer count;

    /**
     * 总价
     */
    private BigDecimal totalPrice;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttr() {
        return skuAttr;
    }

    public void setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 手动计算总价
     */
    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(""+this.count));
    }

}
