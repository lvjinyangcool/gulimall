package com.atguigu.gulimall.cart.controller;

import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
public class CartController {

    @Autowired
    CartService cartService;

    private final String CART_PATH = "redirect:http://cart.gulimall.com/cart.html";


    /**
     * 浏览器有一个cookie:user-key; 标识用户身份，一个月过期
     * 如果第一次使用jd的购物车功能，都会给一个临时的用户身份
     * 浏览器以后保存，以后访问都会带上这个cookie
     *
     * 登录: session有
     * 没登录: 按照cookie里面带来user-key来做
     * 第一次: 如果没有用户，则创建一个临时用户
     *
     * @return
     */
    @GetMapping({"/","/cart.html"})
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        //获取购物车列表
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    /**
     * 添加商品至购物车
     * RedirectAttributes: 会自动将数据添加到url后面
     * @param skuId 商品skuId
     * @param num   商品数量
     * @return 页面
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            Model model, RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId, num);
        redirectAttributes.addAttribute("skuId", skuId);
        // 重定向到成功页面
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    /**
     * 跳转到成功页面
     *
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam(value = "skuId",required = false) Object skuId, Model model){
        CartItem cartItem = new CartItem();
        if(skuId == null){
            model.addAttribute("item", null);
        }else {
            try {
                cartItem = cartService.getCartItem(Long.parseLong((String)skuId));
            } catch (NumberFormatException e) {
                log.warn("恶意操作! 页面传来非法字符.");
            }
            model.addAttribute("item", cartItem);
        }
        return "success";
    }

    /**
     * 删除购物项
     *
     * @param skuId 商品skuId
     * @return  CART_PATH
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return CART_PATH;
    }

    /**
     *  修改商品数量
     *
     * @param skuId  商品skuId
     * @param num    商品数量
     * @return  CART_PATH
     */
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num){
        cartService.changeItemCount(skuId, num);
        return CART_PATH;
    }

    /**
     * 选中/不选中 购物项
     *
     * @param skuId  商品skuId
     * @param check  选中/不选中
     * @return   CART_PATH
     */
    @GetMapping("checkItem.html")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("check") Integer check){
        cartService.checkItem(skuId, check);
        return CART_PATH;
    }

    /**
     * 获取用户的购物车项
     *
     * @return  购物车项列表
     */
    @ResponseBody
    @GetMapping("/currentUserCartItems")
    public List<CartItem> getCurrentUserCartItems(){
        return cartService.getUserCartItems();
    }
}
