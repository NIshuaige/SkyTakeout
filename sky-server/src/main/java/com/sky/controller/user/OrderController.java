/**
 * @Author：乐
 * @Package：com.sky.controller.user
 * @Project：sky-take-out
 * @name：OrderController
 * @Date：2024/3/9 0009  15:53
 * @Filename：OrderController
 */
package com.sky.controller.user;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "用户端订单相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("用户下单")
    private Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.sumbitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }


    /**
     * 查看历史订单
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("查看历史订单")
    private Result<PageResult> historyOrders(int page,int pageSize ,Integer status){
        PageResult pageResult= orderService.pageQuery(page,pageSize,status);

        return Result.success(pageResult);
    }


    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    private Result<OrderVO> orderDetail(@PathVariable Long id){
        log.info("查询订单详情{}",id);
        OrderVO orderVO= orderService.details(id);

        return Result.success(orderVO);
    }
}
