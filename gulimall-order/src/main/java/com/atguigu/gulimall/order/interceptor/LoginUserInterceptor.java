package com.atguigu.gulimall.order.interceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {


    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        MemberRespVo memberRespVo = (MemberRespVo)session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(memberRespVo != null){
            loginUser.set(memberRespVo);
            return true;
        }
        // 没登陆就去登录
        session.setAttribute("msg", AuthServerConstant.NOT_LOGIN);
        response.sendRedirect("http://auth.gulimall.com/login.html");
        return false;
    }
}
