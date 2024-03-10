/**
 * @Author：乐
 * @Package：com.sky.mapper
 * @Project：sky-take-out
 * @name：OrderDetailMapper
 * @Date：2024/3/9 0009  16:06
 * @Filename：OrderDetailMapper
 */
package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 向订单明细表中批量插入数据
     * @param orderDetailArrayList
     */
    void insertBatch(ArrayList<OrderDetail> orderDetailArrayList);


    /**
     * 根据订单id查询菜品详细信息
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
}
