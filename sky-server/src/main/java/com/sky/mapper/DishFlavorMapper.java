/**
 * @Author：乐
 * @Package：com.sky.mapper
 * @Project：sky-take-out
 * @name：DishFlavorMapper
 * @Date：2024/3/1 0001  20:43
 * @Filename：DishFlavorMapper
 */
package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 向口味表中插入数据
     * @param dishFlavor
     */

    void insert(DishFlavor dishFlavor);

    /**
     * 批量插入口味数据
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);
}
