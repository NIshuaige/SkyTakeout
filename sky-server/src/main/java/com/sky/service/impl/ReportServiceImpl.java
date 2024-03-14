/**
 * @Author：乐
 * @Package：com.sky.service.impl
 * @Project：sky-take-out
 * @name：ReportServiceImpl
 * @Date：2024/3/14 0014  16:34
 * @Filename：ReportServiceImpl
 */
package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {

        //处理返回的时间
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            //将从开始到结束之间的时间加入数组中
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        log.info("时间{}",dateList);


        List<Double> turnoverList = new ArrayList<>();
        //查询数组中要求日期的营业额
        for (LocalDate date : dateList) {

            //TODO LocalDateTime的应用
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("beginTime",beginTime);
            map.put("endTime",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover =orderMapper.getByMap(map);
            //当天营业额为空时，转为0
            turnover = turnover == null? 0.00:turnover;

            turnoverList.add(turnover);

        }


        //返回TurnoverReportVO
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();

        return turnoverReportVO;
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //时间列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }


        //新用户列表
        List<Integer> newUserList = new ArrayList<>();
        //总共的用户列表
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);

            //查询新用户
            Map newUserMap = new HashMap<>();
            newUserMap.put("endTime",endTime);
            newUserMap.put("beginTime",beginTime);
            Integer newUsers =userMapper.getUserStatic(newUserMap);
            if (newUsers == null){
                newUsers = 0;
            }
            newUserList.add(newUsers);

            //查询总共的用户
            Map totalUserMap = new HashMap<>();
            totalUserMap.put("endTime",endTime);
            Integer totalUsers =userMapper.getUserStatic(totalUserMap);
            totalUserList.add(totalUsers);

        }


        //返回UserReportVO对象
        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();


        return userReportVO;
    }


    /**
     * 订单统计
     * @param begin
     * @param end
     */
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //时间
        //时间列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //每日订单
        List<Integer> orderList = new ArrayList<>();
        //有效订单
        List<Integer> vaildOrderList = new ArrayList<>();
        //订单总数
        Integer totalOrderCount = 0;
        //有效订单总数
        Integer validOrderCount = 0;
        ////订单完成率


        for (LocalDate date : dateList) {
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);

            //查询每日订单
            Map orderMap = new HashMap<>();
            orderMap.put("endTime",endTime);
            orderMap.put("beginTime",beginTime);
            Integer order = orderMapper.getOrderCount(orderMap);
            if (order == null){
                order = 0;
            }

            orderList.add(order);
            totalOrderCount = totalOrderCount+order;

            //查询有效订单
            Map validOrderMap = new HashMap<>();
            validOrderMap.put("endTime",endTime);
            validOrderMap.put("beginTime",beginTime);
            validOrderMap.put("status",Orders.COMPLETED);
            Integer vaildOrder =orderMapper.getOrderCount(validOrderMap);
            if (vaildOrder == null){
                vaildOrder = 0;
            }

            vaildOrderList.add(vaildOrder);
            validOrderCount = vaildOrder + validOrderCount;
        }

        //订单完成率
        Double orderCompletionRate =0.0;
        if (totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue()/totalOrderCount;
        }


        //返回OrderReportVO对象
        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCountList(StringUtils.join(vaildOrderList, ","))
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();


        return orderReportVO;
    }
}
