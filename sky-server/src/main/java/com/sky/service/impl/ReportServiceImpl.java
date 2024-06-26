/**
 * @Author：乐
 * @Package：com.sky.service.impl
 * @Project：sky-take-out
 * @name：ReportServiceImpl
 * @Date：2024/3/14 0014  16:34
 * @Filename：ReportServiceImpl
 */
package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;
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


    /**
     * 查询销量排名top10
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTOP10(LocalDate begin, LocalDate end) {
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);

        Map map = new HashMap<>();
        map.put("beginTime",beginTime);
        map.put("endTime",endTime);
        map.put("status", Orders.COMPLETED);
        List<GoodsSalesDTO> salesTop10ReportVOS=orderMapper.getTOP10(map);
        //TODO stream()
        List<String> names = salesTop10ReportVOS.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");
        List<Integer> numbers = salesTop10ReportVOS.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();

        return salesTop10ReportVO;
    }


    /**
     * 导出运营数据报表
     * @param response
     */
    public void exportBusinessData(HttpServletResponse response) throws IOException {
        //1.获取要插入表格的数据 -- 查询最近30天的运营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        //查询概览数据
        BusinessDataVO businessData = workspaceService.getBusinessData(
                LocalDateTime.of(dateBegin, LocalTime.MIN),
                LocalDateTime.of(dateEnd, LocalTime.MAX)
        );


        //2.获取excel模板表格
        //获取模板文件
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/model.xlsx");

        try {
            //基于模板文件创建一个新文件
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //3.使用POI将数据插入表格中
            XSSFSheet sheet = excel.getSheetAt(0);
            //填入时间段
            sheet.getRow(1).getCell(1).setCellValue("时间："+dateBegin+"至"+dateEnd);

            //获取第四行和第五行
            XSSFRow row3 = sheet.getRow(3);
            XSSFRow row4 = sheet.getRow(4);
            /*将概览数据插入表格中*/
            //插入营业额
            row3.getCell(2).setCellValue(businessData.getTurnover());
            //插入订单完成率
            row3.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            //插入新增用户数
            row3.getCell(6).setCellValue(businessData.getNewUsers());
            //插入有效订单
            row4.getCell(2).setCellValue(businessData.getValidOrderCount());
            //插入评价客单价
            row4.getCell(4).setCellValue(businessData.getUnitPrice());


            /*插入明细数据*/
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                //当天的营业数据
                BusinessDataVO businessData1 = workspaceService.getBusinessData(
                        LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX)
                );

                //获取行
                XSSFRow row = sheet.getRow(i + 7);
                //获取单元格，并插入明细数据
                //插入日期
                row.getCell(1).setCellValue(date.toString());
                //插入营业额
                row.getCell(2).setCellValue(businessData.getTurnover());
                //插入订单完成率
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                //插入新增用户数
                row.getCell(6).setCellValue(businessData.getNewUsers());
                //插入有效订单
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                //插入评价客单机
                row.getCell(5).setCellValue(businessData.getUnitPrice());

            }

            //通过输出流将excel文件下载到客户端浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            excel.close();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }





    }
}
