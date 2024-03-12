/**
 * @Author：乐
 * @Package：com.sky.service
 * @Project：sky-take-out
 * @name：OrderService
 * @Date：2024/3/9 0009  15:56
 * @Filename：OrderService
 */
package com.sky.service;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 用户下单
     * @return
     */
    OrderSubmitVO sumbitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     *
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult pageQuery(int page, int pageSize, Integer status);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO details(Long id);


    /**
     * 取消订单
     * @param id
     */
    void cancel(Long id);

    /**
     * 在来一单
     * @param id
     */
    void repetition(Long id);

    /**
     * 各个状态的订单数
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 订单搜索
     * @param
     * @return
     */
    PageResult conditionSearch(int page,int pageSize ,Integer status);

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 派送订单
     * @param id
     */
    void delivery(Long id);

    /**
     * 完成订单
     * @param id
     */
    void complete(Long id);

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 客户端取消订单
     * @param ordersCancelDTO
     */
    void cancelByAdmin(OrdersCancelDTO ordersCancelDTO);
}
