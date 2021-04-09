package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author yanglvjin
 * @email yanglvjin@gmail.com
 * @date 2021-04-10 00:44:34
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
