package com.atguigu.gulimall.ware.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.WareInfoEntity;
import com.atguigu.gulimall.ware.vo.FareVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author yanglvjin
 * @email yanglvjin@gmail.com
 * @date 2021-04-10 00:49:28
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据用户地址计算运费
     *
     * @param addrId  地址id
     * @return        运费
     */
    FareVo getFare(Long addrId);
}

