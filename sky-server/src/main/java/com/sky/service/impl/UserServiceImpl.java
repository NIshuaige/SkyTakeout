/**
 * @Author：乐
 * @Package：com.sky.service.impl
 * @Project：sky-take-out
 * @name：UserServiceImpl
 * @Date：2024/3/7 0007  19:18
 * @Filename：UserServiceImpl
 */
package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.constant.WXLoginConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String GRANDTYPE = "authorization_code";
    public static final String WX_LOGIN="https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    public User wxLogin(UserLoginDTO userLoginDTO) {
        log.info("微信登录：{}",userLoginDTO);
        String openid = openId(userLoginDTO.getCode());

        //判断openid是否为空，为空则报出异常
        if (openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断该用户是否为新用户
        User user= userMapper.getByOpenId(openid);

        if (user == null){
            //是新用户则完成自动注册
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        log.info("用户信息{}",user);
        //返回用户对象
        return user;
    }

    public String openId(String code){
        //通过微信接口服务获取openid
        Map<String, String> map = new HashMap<>();
        map.put(WXLoginConstant.APPID,weChatProperties.getAppid());
        map.put(WXLoginConstant.SECRET,weChatProperties.getSecret());
        map.put(WXLoginConstant.JS_CODE,code);
        map.put(WXLoginConstant.GRANT_TYPE,GRANDTYPE);
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
