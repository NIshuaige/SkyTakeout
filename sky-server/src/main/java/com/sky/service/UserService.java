/**
 * @Author：乐
 * @Package：com.sky.service
 * @Project：sky-take-out
 * @name：UserService
 * @Date：2024/3/7 0007  19:17
 * @Filename：UserService
 */
package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
