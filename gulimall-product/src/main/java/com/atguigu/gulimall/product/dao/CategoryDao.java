package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author yanglvjin
 * @email yanglvjin@gmail.com
 * @date 2021-04-09 23:16:38
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
