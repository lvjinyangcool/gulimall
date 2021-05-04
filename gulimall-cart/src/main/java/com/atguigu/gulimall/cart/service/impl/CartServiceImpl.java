package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    /**
     * 购物车key前缀
     */
    private final String CART_PREFIX = "yang:cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        String response = (String) cartOps.get(skuId.toString());
        if(StringUtils.isNotEmpty(response)){
            CartItem cartItem = JSON.parseObject(response, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            cartOps.put(skuId.toString(), JSON.toJSON(cartItem));
            return cartItem;
        }
        CartItem cartItem = new CartItem();
        CompletableFuture<Void> getSkuInfo = CompletableFuture.runAsync(() -> {
            // 1. 远程查询当前要添加的商品的信息
            R skuInfo = productFeignService.SkuInfo(skuId);
            SkuInfoVo sku = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {});
            // 2.商品添加到购物车
            cartItem.setCheck(true);
            cartItem.setImage(sku.getSkuDefaultImg());
            cartItem.setCount(num);
            cartItem.setPrice(sku.getPrice());
            cartItem.setTitle(sku.getSkuTitle());
            cartItem.setSkuId(skuId);
        }, executor);
        //3.远程查询sku的组合信息
        CompletableFuture<Void> getSaleAttrs = CompletableFuture.runAsync(() -> {
            List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
            cartItem.setSkuAttr(skuSaleAttrValues);
        }, executor);
        CompletableFuture.allOf(getSaleAttrs, getSkuInfo).get();
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
        return cartItem;

    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Cart cart = new Cart();
        //临时购物车
        String tempCartKey = CART_PREFIX + userInfoTo.getUserKey();
        if(userInfoTo.getUserId() != null){
            // 1. 已登录 对用户的购物车进行操作
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            // 1.1 如果临时购物车的数据没有进行合并
            List<CartItem> tempCartItemList = this.getCartItems(tempCartKey);
            if(!CollectionUtils.isEmpty(tempCartItemList)){
                // 1.2 临时购物车有数据 则进行合并
                log.info("\n[" + userInfoTo.getUsername() + "] 的购物车已合并");
                for (CartItem cartItem : tempCartItemList) {
                    addToCart(cartItem.getSkuId(), cartItem.getCount());
                }
                // 1.3 清空临时购物车
                this.clearCart(tempCartKey);
            }
            // 1.4 获取登录后的购物车数据 [包含合并过来的临时购物车数据]
            List<CartItem> cartItems = this.getCartItems(cartKey);
            cart.setItems(cartItems);
        }else {
            // 2. 没登录 获取临时购物车的所有购物项
            cart.setItems(this.getCartItems(tempCartKey));
        }
        return cart;
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        String result = (String)cartOps.get(skuId.toString());
        return JSON.parseObject(result, CartItem.class);
    }

    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        CartItem cartItem = this.getCartItem(skuId);
        cartItem.setCheck(check == 1);
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        CartItem cartItem = this.getCartItem(skuId);
        cartItem.setCount(num);
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public BigDecimal toTrade() throws ExecutionException, InterruptedException {
        BigDecimal amount = this.getCart().getTotalAmount();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        redisTemplate.delete(CART_PREFIX + (userInfoTo.getUserId() != null ? userInfoTo.getUserId().toString() : userInfoTo.getUserKey()));
        return amount;
    }


    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(userInfoTo.getUserId() != null){
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            List<CartItem> cartItems = this.getCartItems(cartKey);
            if(!CollectionUtils.isEmpty(cartItems)){
                // 获取所有被选中的购物项
                List<CartItem> collect = cartItems.stream().filter(CartItem::getCheck).peek(item -> {
                    try {
                        R r = productFeignService.getPrice(item.getSkuId());
                        String price = (String) r.get("data");
                        item.setPrice(new BigDecimal(price));
                    } catch (Exception e) {
                        log.warn("远程查询商品价格出错 [商品服务未启动]");
                    }
                }).collect(Collectors.toList());
                return collect;
            }
        }
        return null;
    }

    /**
     * 获取购物车所有项
     *
     * @param cartKey 购物车key
     * @return 所有的购物车项
     */
    private List<CartItem> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if(values != null && values.size() > 0){
            return values.stream().map(obj -> JSON.parseObject((String) obj, CartItem.class)).collect(Collectors.toList());
        }
        return null;
    }


    /**
     * 获取到我们要操作的购物车 [已经包含用户前缀 只需要带上用户id 或者临时id 就能对购物车进行操作]
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        // 1. 这里我们需要知道操作的是离线购物车还是在线购物车
        String cartKey = CART_PREFIX;
        if(userInfoTo != null && userInfoTo.getUserId() != null){
            // 已登录的用户购物车的标识
            cartKey += userInfoTo.getUserId();
        }else {
            // 未登录的用户购物车的标识
            cartKey += userInfoTo.getUserKey();
        }
        // 绑定这个 key 以后所有对redis 的操作都是针对这个key
        return redisTemplate.boundHashOps(cartKey);
    }
}
