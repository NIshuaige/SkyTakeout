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
import org.apache.ibatis.annotations.Select;

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


    /**
     * 动态修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 待接单数量
     * @return
     */
    @Select("select  count(*) from orders where status = 2")
    Integer getToBeConfirmed();

    /**
     * 待派送数量
     * @return
     */
    @Select("select  count(*) from orders where status = 3")
    Integer getConfirmed();

    /**
     * 派送中数量
     * @return
     */
    @Select("select count(*) from orders where status = 4")
    Integer getDeliveryInProgress();
}
