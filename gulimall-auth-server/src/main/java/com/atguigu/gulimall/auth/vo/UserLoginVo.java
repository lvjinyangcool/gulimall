package com.atguigu.gulimall.auth.vo;

import lombok.Data;

/**
 * <p>Title: UserLoginVo</p>
 * Description：
 * date：2020/6/25 21:38
 */
@Data
public class UserLoginVo {

	/**
	 * 账号
	 */
	private String loginAccount;

	/**
	 * 密码
	 */
	private String password;
}
