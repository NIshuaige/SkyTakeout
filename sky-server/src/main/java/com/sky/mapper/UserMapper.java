/**
 * @Author：乐
 * @Package：com.sky.mapper
 * @Project：sky-take-out
 * @name：UserMapper
 * @Date：2024/3/7 0007  20:02
 * @Filename：UserMapper
 */
package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {


    /**
     * 根据openid查询用户信息
     * @param openid
     * @return
     */
    @Select("select * from user where openid=#{openid}")
    User getByOpenId(String openid);

    /**
     * 插入用户数据
     * @param user
     */
    void insert(User user);

    /**
     * 根据参数动态查询用户数
     * @param newUserMap
     * @return
     */
    Integer getUserStatic(Map newUserMap);
}
