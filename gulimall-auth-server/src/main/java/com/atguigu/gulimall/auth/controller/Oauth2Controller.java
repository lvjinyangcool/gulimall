package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理社交登录请求 (QQ丶微信丶微博登录等功能)
 */
@Slf4j
@Controller
@RequestMapping("/oauth2.0")
public class Oauth2Controller {

    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/logout")
    public String login(HttpSession session){
        if(session.getAttribute(AuthServerConstant.LOGIN_USER) != null){
            log.info("\n[" + ((MemberRespVo)session.getAttribute(AuthServerConstant.LOGIN_USER)).getUsername() + "] 已下线");
        }
        session.invalidate();
        return "redirect:http://auth.gulimall.com/login.html";
    }


    @GetMapping("/weibo/success")
    public String weiboAccessLogin(@RequestParam("code") String code, HttpSession session) throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("client_id", "1294828100");
        map.put("client_secret", "a8e8900e15fba6077591cdfa3105af44");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code", code);
        Map<String, String> headers = new HashMap<>();
        //1.根据code获取AccessToken
        HttpResponse response = HttpUtils.doPost("api.weibo.com", "/oauth2/access_token", "post", headers, null, map);

        //2.处理http请求结果
        if(response.getStatusLine().getStatusCode() == 200){
            //获取到accessToken
            String accessTokenJson = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(accessTokenJson, SocialUser.class);
            // 相当于我们知道了当前是那个用户
            // 1.如果用户是第一次进来 自动注册进来(为当前社交用户生成一个会员信息 以后这个账户就会关联这个账号)

            R login = memberFeignService.socialLogin(socialUser);
            if(login.getCode() == 0){
                MemberRespVo respVo = login.getData("data" ,new TypeReference<MemberRespVo>() {});

                log.info("\n欢迎 [" + respVo.getUsername() + "] 使用社交账号登录");
                // 第一次使用session 命令浏览器保存这个用户信息 JESSIONSEID 每次只要访问这个网站就会带上这个cookie
                // 在发卡的时候扩大session作用域 (指定域名为父域名)
                // TODO 1.默认发的当前域的session (需要解决子域session共享问题)
                // TODO 2.使用JSON的方式序列化到redis
                //使用springSession解决上面两个问题 GulimallSessionConfig配置文件
//				new Cookie("JSESSIONID","").setDomain("gulimall.com");
                session.setAttribute(AuthServerConstant.LOGIN_USER, respVo);
                // 登录成功 跳回首页
                return "redirect:http://gulimall.com";
            }else{
                return "redirect:http://auth.gulimall.com/login.html";
            }
        }else{
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}
