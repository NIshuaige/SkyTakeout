/**
 * @Author：乐
 * @Package：com.sky.controller.admin
 * @Project：sky-take-out
 * @name：OrderController
 * @Date：2024/3/12 0012  13:56
 * @Filename：OrderController
 */
package com.sky.controller.admin;


import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "商家端订单管理相关接口")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;


    /**
     * 订单搜索
     * @param
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    private Result<PageResult> conditionSearch(int page,int pageSize ,Integer status){
        log.info("订单搜索：");
        PageResult pageResult = orderService.conditionSearch(page, pageSize ,status);

        return Result.success(pageResult);
    }




    /**
     * 各个状态的订单数
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数")
    private Result<OrderStatisticsVO> statistics(){
        log.info("各个状态的订单数：");
        OrderStatisticsVO orderStatisticsVO= orderService.statistics();

        return Result.success(orderStatisticsVO);
    }


    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    private Result<OrderVO> detaile(@PathVariable Long id){
        log.info("查询订单详情{}",id);
        OrderVO orderVO = orderService.details(id);

        return Result.success(orderVO);
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    private Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("接单：{}",ordersConfirmDTO);
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }


    /**
     * 派送订单
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    private Result delivery(@PathVariable Long id){
        log.info("派送订单{}",id);
        orderService.delivery(id);

        return  Result.success();
    }


    /**
     * 完成订单
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    private Result complete(@PathVariable Long id){
        log.info("完成订单{}",id);
        orderService.complete(id);

        return Result.success();
    }


    /**
     * 拒单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    private Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("拒单：{}",ordersRejectionDTO);
        orderService.rejection(ordersRejectionDTO);

        return Result.success();
    }


    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    private Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("取消订单：{}",ordersCancelDTO);
        orderService.cancelByAdmin(ordersCancelDTO);

        return Result.success();
    }
}
