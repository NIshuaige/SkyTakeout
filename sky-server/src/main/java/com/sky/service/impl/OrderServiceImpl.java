/**
 * @Author：乐
 * @Package：com.sky.service.impl
 * @Project：sky-take-out
 * @name：OrderServiceImol
 * @Date：2024/3/9 0009  15:57
 * @Filename：OrderServiceImol
 */
package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * 用户下单
     * @return
     */
    @Transactional
    public OrderSubmitVO sumbitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //处理各种业务异常（地址簿为空，购物车为空）
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null){
            throw  new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        ShoppingCart cart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        cart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(cart);
        if (shoppingCartList == null || shoppingCartList.size() == 0){
            throw new  ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //向订单表中插入数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);

        orderMapper.insert(orders);


        //向菜品表中插入数据
        ArrayList<OrderDetail> orderDetailArrayList = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();//订单明细
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailArrayList.add(orderDetail);
        }

        orderDetailMapper.insertBatch(orderDetailArrayList);

        //清空购物车

        shoppingCartMapper.deleteById(userId);
        //封装VO返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .build();

        return orderSubmitVO;
    }


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    public PageResult pageQuery(int page, int pageSize, Integer status) {
        PageHelper.startPage(page,pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        Page<Orders> pageQuery =orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> orderVOList = new ArrayList<>();

        for (Orders order : pageQuery) {
            Long orderId = order.getId();

            List<OrderDetail> orderDetails=orderDetailMapper.getByOrderId(orderId);

            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order,orderVO);
            orderVO.setOrderDetailList(orderDetails);

            orderVOList.add(orderVO);

        }

        return new PageResult(pageQuery.getTotal(),orderVOList);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    public OrderVO details(Long id) {
        OrdersDTO ordersDTO = new OrdersDTO();
        ordersDTO.setId(id);
        ordersDTO.setUserId(BaseContext.getCurrentId());

        //查询菜品
        Orders orders = orderMapper.getDetail(ordersDTO);

        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetailList);


        return orderVO;
    }
}
