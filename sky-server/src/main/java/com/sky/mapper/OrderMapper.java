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
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    /**
     * 处理待付款订单超时
     * @param status
     * @param localDateTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{localDateTime}")
    List<Orders> getByStatusAndOrderTimeTL(Integer status, LocalDateTime localDateTime);

    /**
     * 动态获取满足条件的订单
     * @param ordersDTO
     * @return
     */
    List<Orders> getOrders(OrdersDTO ordersDTO);

    /**
     * 根据动态条件返回营业额
     * @param map
     * @return
     */
    Double getByMap(Map map);


    /**
     * 根据参数动态返回订单数
     * @param orderMap
     * @return
     */
    Integer getOrderCount(Map orderMap);

    /**
     * 查询菜品top10
     * @param map
     */
    List<GoodsSalesDTO> getTOP10(Map map);

    /**
     *
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
