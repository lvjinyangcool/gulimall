package com.atguigu.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UserNameExistException;
import com.atguigu.gulimall.member.service.MemberService;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegisterVo;
import com.atguigu.gulimall.member.vo.SocialUser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Resource
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo memberRegisterVo) {
        MemberDao memberDao = this.baseMapper;
        MemberEntity entity = new MemberEntity();
        // 设置默认等级
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
        entity.setLevelId(memberLevelEntity.getId());


        // 检查手机号 用户名是否唯一
        this.checkPhone(memberRegisterVo.getPhone());
        this.checkUserName(memberRegisterVo.getUserName());

        entity.setMobile(memberRegisterVo.getPhone());
        entity.setUsername(memberRegisterVo.getUserName());

        // 密码要加密存储
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        entity.setPassword(bCryptPasswordEncoder.encode(memberRegisterVo.getPassword()));
        // 其他的默认信息
        entity.setCity("厦门");
        entity.setCreateTime(new Date());
        entity.setStatus(0);
        entity.setNickname(memberRegisterVo.getUserName());
        entity.setBirth(new Date());
        entity.setEmail("xxx@gmail.com");
        entity.setGender(1);
        entity.setJob("JAVA");
        baseMapper.insert(entity);
        memberDao.insert(entity);
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginAccount = vo.getLoginAccount();
        String password = vo.getPassword();

        MemberDao memberDao = this.baseMapper;
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        // 去数据库查询
        MemberEntity entity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginAccount).or().eq("mobile", loginAccount));
        if(entity == null){
            // 登录失败
            return null;
        }
        // 前面传一个明文密码 后面传一个编码后的密码
        boolean matches = bCryptPasswordEncoder.matches(password, entity.getPassword());
        if (matches){
            entity.setPassword(null);
            return entity;
        }
        return null;
    }

    @Override
    public MemberEntity socialLogin(SocialUser socialUser) {
        //登录和注册合并逻辑
        // 微博的uid
        String uid = socialUser.getUid();
        // 1.判断社交用户登录过系统
        MemberDao dao = this.baseMapper;
        MemberEntity entity = dao.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));

        MemberEntity memberEntity = new MemberEntity();
        if(entity != null){
            // 说明这个用户注册过, 修改它的资料
            memberEntity.setId(entity.getId());
            memberEntity.setAccessToken(socialUser.getAccessToken());
            memberEntity.setExpiresIn(socialUser.getExpiresIn());
            // 更新
            dao.updateById(memberEntity);
            entity.setAccessToken(socialUser.getAccessToken());
            entity.setExpiresIn(socialUser.getExpiresIn());
            entity.setPassword(null);
            return entity;
        }else{
            // 2. 没有查到当前社交用户对应的记录 我们就需要注册一个
            HashMap<String, String> map = new HashMap<>();
            map.put("access_token", socialUser.getAccessToken());
            map.put("uid", socialUser.getUid());
            try {
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), map);
                // 3. 查询当前社交用户账号信息(昵称、性别等)
                if(response.getStatusLine().getStatusCode() == 200){
                    // 查询成功
                    String json = EntityUtils.toString(response.getEntity());
                    // 这个JSON对象什么样的数据都可以直接获取
                    JSONObject jsonObject = JSON.parseObject(json);
                    memberEntity.setNickname(jsonObject.getString("name"));
                    memberEntity.setUsername(jsonObject.getString("name"));
                    memberEntity.setGender("m".equals(jsonObject.getString("gender"))?1:0);
                    memberEntity.setCity(jsonObject.getString("location"));
                    memberEntity.setJob("自媒体");
                    memberEntity.setEmail(jsonObject.getString("email"));
                }
            } catch (Exception e) {
                log.warn("社交登录时远程调用出错 [尝试修复]");
            }
            memberEntity.setStatus(0);
            memberEntity.setCreateTime(new Date());
            memberEntity.setBirth(new Date());
            memberEntity.setLevelId(1L);
            memberEntity.setSocialUid(socialUser.getUid());
            memberEntity.setAccessToken(socialUser.getAccessToken());
            memberEntity.setExpiresIn(socialUser.getExpiresIn());

            // 注册 -- 登录成功
            dao.insert(memberEntity);
            memberEntity.setPassword(null);
            return memberEntity;
        }
    }


    private void checkPhone(String phone) throws PhoneExistException{
        if(this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone)) > 0){
            throw new PhoneExistException();
        }
    }

    private void checkUserName(String username) throws UserNameExistException{
        if(this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username)) > 0){
            throw new UserNameExistException();
        }
    }
}