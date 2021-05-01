package com.atguigu.gulimall.member.vo;

import lombok.Data;

@Data
public class MemberLoginVo {
    /**
     * 账号
     */
    private String loginAccount;

    /**
     * 密码
     */
    private String password;
}
