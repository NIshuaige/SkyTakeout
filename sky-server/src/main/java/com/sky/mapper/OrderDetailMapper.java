/**
 * @Author：乐
 * @Package：com.sky.mapper
 * @Project：sky-take-out
 * @name：OrderDetailMapper
 * @Date：2024/3/9 0009  16:06
 * @Filename：OrderDetailMapper
 */
package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface OrderDetailMapper {

    /**
     * 向订单明细表中批量插入数据
     * @param orderDetailArrayList
     */
    void insertBatch(ArrayList<OrderDetail> orderDetailArrayList);
}
