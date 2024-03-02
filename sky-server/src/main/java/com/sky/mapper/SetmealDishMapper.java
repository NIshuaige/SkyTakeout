/**
 * @Author：乐
 * @Package：com.sky.mapper
 * @Project：sky-take-out
 * @name：SeteamlDishMapper
 * @Date：2024/3/2 0002  16:17
 * @Filename：SeteamlDishMapper
 */
package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询对应的套餐id
     * @param
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}
