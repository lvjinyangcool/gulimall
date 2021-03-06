package com.atguigu.gulimall.member.controller;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UserNameExistException;
import com.atguigu.gulimall.member.feign.CouponFeignService;
import com.atguigu.gulimall.member.service.MemberService;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegisterVo;
import com.atguigu.gulimall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;


/**
 * 会员
 *
 * @author yanglvjin
 * @email yanglvjin@gmail.com
 * @date 2021-04-10 00:44:34
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
//
    @Autowired
    CouponFeignService couponFeignService;


    @RequestMapping("/coupons")
    public R test(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");

        R memberCoupons = couponFeignService.membercoupons();

        return Objects.requireNonNull(R.ok().put("member", memberEntity)).put("coupons",memberCoupons.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/register")
    public R register(@RequestBody MemberRegisterVo memberRegisterVo){

        try {
            memberService.register(memberRegisterVo);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UserNameExistException e) {
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(), BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo memberLoginVo){

        MemberEntity entity =  memberService.login(memberLoginVo);
        if(entity != null){
            return R.ok().setData(entity);
        }else {
            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_ERROR.getCode(), BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_ERROR.getMsg());
        }
    }

    /**
     * 社交登录
     *
     * @param socialUser
     * @return
     */
    @PostMapping("/oauth2/login")
    public R socialLogin(@RequestBody SocialUser socialUser){
        MemberEntity entity =  memberService.socialLogin(socialUser);
        if(entity != null){
            return R.ok().setData(entity);
        }else {
            return R.error(BizCodeEnum.SOCIAL_USER_LOGIN_ERROR.getCode(), BizCodeEnum.SOCIAL_USER_LOGIN_ERROR.getMsg());
        }

    }
}
