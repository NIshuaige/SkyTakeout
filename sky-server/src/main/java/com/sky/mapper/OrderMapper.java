/**
 * @Author：乐
 * @Package：com.sky.mapper
 * @Project：sky-take-out
 * @name：OrderMapper
 * @Date：2024/3/9 0009  16:03
 * @Filename：OrderMapper
 */
package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 分页
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 动态查询订单
     * @param ordersDTO
     * @return
     */
    Orders getDetail(OrdersDTO ordersDTO);
}
