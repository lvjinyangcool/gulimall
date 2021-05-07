package com.atguigu.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.dao.WareInfoDao;
import com.atguigu.gulimall.ware.entity.WareInfoEntity;
import com.atguigu.gulimall.ware.feign.MemberFeignService;
import com.atguigu.gulimall.ware.service.WareInfoService;
import com.atguigu.gulimall.ware.vo.FareVo;
import com.atguigu.gulimall.ware.vo.MemberAddressVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                new QueryWrapper<WareInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addrId) {
        R info = memberFeignService.addrInfo(addrId);
        FareVo fareVo = new FareVo();
        MemberAddressVo addressVo = info.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {});
        fareVo.setMemberAddressVo(addressVo);
        if(addressVo != null){
            String phone = addressVo.getPhone();
            if(phone == null || phone.length() < 2){
                phone = new Random().nextInt(100) + "";
            }
            BigDecimal decimal = new BigDecimal(phone.substring(phone.length() - 1));
            fareVo.setFare(decimal);
        }else{
            fareVo.setFare(new BigDecimal("20"));
        }
        return fareVo;
    }

}