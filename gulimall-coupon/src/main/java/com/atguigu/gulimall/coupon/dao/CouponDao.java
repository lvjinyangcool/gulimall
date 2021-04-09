package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author yanglvjin
 * @email yanglvjin@email.com
 * @date 2021-04-10 01:03:44
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
