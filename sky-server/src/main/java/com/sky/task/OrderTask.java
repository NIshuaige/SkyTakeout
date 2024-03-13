/**
 * @Author：乐
 * @Package：com.sky.task
 * @Project：sky-take-out
 * @name：OrderTask
 * @Date：2024/3/13 0013  20:10
 * @Filename：OrderTask
 */
package com.sky.task;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理待付款订单超时
     */
    @Scheduled(cron = "0 * * * * ? ")//每分钟触发一次
    public void processOrderTimeOut(){
        log.info("处理待付款订单超时：{}", LocalDateTime.now());

        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-15);

        List<Orders>  ordersList = orderMapper.getByStatusAndOrderTimeTL(Orders.PENDING_PAYMENT,localDateTime);

        for (Orders orders : ordersList) {
            orders.setStatus(Orders.CANCELLED);
            orders.setCancelTime(LocalDateTime.now());
            orders.setCancelReason("订单已超时");

            orderMapper.update(orders);
        }
    }


    /**
     * 处理派送中的订单，用户一直不点完成
     */
    @Scheduled(cron = "0 0 3 * * ? ")//每天的上午3点触发一次
//    @Scheduled(cron = "0/5 * * * * ? ")
    public void processDeliveryOrder(){
        log.info("处理派送中的订单，用户一直不点完成：{}",LocalDateTime.now());

        LocalDateTime localDateTime = LocalDateTime.now().plusHours(-3);

        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeTL(Orders.DELIVERY_IN_PROGRESS, localDateTime);
        for (Orders orders : ordersList) {
            orders.setStatus(Orders.COMPLETED);

            orderMapper.update(orders);
        }
    }
}
