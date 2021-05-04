package com.atguigu.gulimall.cart.interceptor;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 在执行目标方法之前，判断用户的登录状态，并封装传递给controller目标请求
 */
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    /**
     * 目标方法执行之前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo user = new UserInfoTo();

        HttpSession session = request.getSession();
        MemberRespVo member = (MemberRespVo)session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(member != null){
            //用户登录
            user.setUserId(member.getId());
            user.setUsername(member.getUsername());
        }
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length > 0){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if(name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    user.setUserKey(cookie.getValue());
                    user.setTempUser(true);
                }
            }
        }
        // 如果没有临时用户 则分配一个临时用户
        if (StringUtils.isEmpty(user.getUserKey())){
            String uuid = UUID.randomUUID().toString().replace("-","");
            user.setUserKey("YANG-" + uuid);
        }
        threadLocal.set(user);
        return true;
    }

    /**
       执行完毕之后分配临时用户让浏览器保存

     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        UserInfoTo userInfoTo = threadLocal.get();
        if(!userInfoTo.isTempUser()){
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            // 设置这个cookie作用域 过期时间
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIME_OUT);
            response.addCookie(cookie);
        }

    }
}
