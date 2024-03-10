/**
 * @Author：乐
 * @Package：com.sky.service
 * @Project：sky-take-out
 * @name：OrderService
 * @Date：2024/3/9 0009  15:56
 * @Filename：OrderService
 */
package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
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
}
