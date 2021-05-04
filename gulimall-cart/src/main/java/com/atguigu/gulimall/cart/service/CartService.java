package com.atguigu.gulimall.cart.service;

import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 购物车服务
 */
public interface CartService {
    /**
     * 添加商品至购物车
     *
     * @param skuId 商品skuId
     * @param num   商品数量
     * @return     购物项
     */
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 获取整个购物车
     */
    Cart getCart() throws ExecutionException, InterruptedException;

    /**
     * 获取购物车中某个购物项
     */
    CartItem getCartItem(Long skuId);

    /**
     * 清空购物车
     *
     * @param cartKey 购物车key
     */
    void clearCart(String cartKey);

    /**
     * 勾选购物项
     *
     * @param skuId  商品skuId
     * @param check  是否选中商品
     */
    void checkItem(Long skuId, Integer check);

    /**
     * 改变购物车中物品的数量
     *
     * @param skuId  商品skuId
     * @param num    商品数量
     */
    void changeItemCount(Long skuId, Integer num);

    /**
     * 删除购物项
     *
     * @param skuId  商品skuId
     */
    void deleteItem(Long skuId);

    /**
     * 结账
     */
    BigDecimal toTrade() throws ExecutionException, InterruptedException;

    /**
     * 获取用户的购物车项
     *
     * @return  购物车项列表
     */
    List<CartItem> getUserCartItems();

}
