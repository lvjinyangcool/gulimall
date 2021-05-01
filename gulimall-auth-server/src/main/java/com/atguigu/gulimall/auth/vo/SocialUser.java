package com.atguigu.gulimall.auth.vo;

import lombok.Data;

/**
 * weibo登录  code获取AccessToken的http请求结果
 */
@Data
public class SocialUser {

    private String accessToken;

    private String remindIn;

    private int expiresIn;

    private String uid;

    private String isRealName;
}