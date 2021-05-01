package com.atguigu.gulimall.member.vo;

import lombok.Data;

/**
 * weibo登录  code获取AccessToken的http请求结果
 */
@Data
public class SocialUser {

    private String accessToken;

    private String remindIn;

    private String expiresIn;

    private String uid;

    private String isRealName;
}