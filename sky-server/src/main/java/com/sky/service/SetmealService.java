/**
 * @Author：乐
 * @Package：com.sky.service
 * @Project：sky-take-out
 * @name：SetmealService
 * @Date：2024/3/3 0003  12:49
 * @Filename：SetmealService
 */
package com.sky.service;

import com.sky.dto.SetmealDTO;

public interface SetmealService {


    /**
     * 新增套餐
     * @param setmealDTO
     */
    void insert(SetmealDTO setmealDTO);
}
