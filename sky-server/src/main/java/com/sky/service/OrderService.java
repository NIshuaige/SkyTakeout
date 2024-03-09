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
import com.sky.vo.OrderSubmitVO;

public interface OrderService {

    /**
     * 用户下单
     * @return
     */
    OrderSubmitVO sumbitOrder(OrdersSubmitDTO ordersSubmitDTO);
}
