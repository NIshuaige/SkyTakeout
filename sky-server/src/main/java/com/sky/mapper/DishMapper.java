/**
 * @Author：乐
 * @Package：com.sky.mapper
 * @Project：sky-take-out
 * @name：DishMapper
 * @Date：2024/2/29 0029  19:25
 * @Filename：DishMapper
 */
package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
}
