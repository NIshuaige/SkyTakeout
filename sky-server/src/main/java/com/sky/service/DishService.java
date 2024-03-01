/**
 * @Author：乐
 * @Package：com.sky.service
 * @Project：sky-take-out
 * @name：DishService
 * @Date：2024/2/29 0029  22:05
 * @Filename：DishService
 */
package com.sky.service;

import com.sky.dto.DishDTO;

public interface DishService {

    /**
     * 新增菜品及其口味
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);
}
