/**
 * @Author：乐
 * @Package：com.sky.service
 * @Project：sky-take-out
 * @name：ReportService
 * @Date：2024/3/14 0014  16:34
 * @Filename：ReportService
 */
package com.sky.service;

import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    UserReportVO userStatistics(LocalDate begin, LocalDate end);
}
