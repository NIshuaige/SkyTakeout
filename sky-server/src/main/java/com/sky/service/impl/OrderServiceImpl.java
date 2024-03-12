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
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        //收货地址
        String address = addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail();
        orders.setAddress(address);

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

        //查询订单
        Orders orders = orderMapper.getDetail(ordersDTO);
        //查看订单关联的菜品
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetailList);


        return orderVO;
    }


    /**
     * 取消订单
     * @param id
     */
    public void cancel(Long id) {
        OrdersDTO ordersDTO = new OrdersDTO();
        ordersDTO.setUserId(BaseContext.getCurrentId());
        ordersDTO.setId(id);
        Orders detail = orderMapper.getDetail(ordersDTO);
        Integer status = detail.getStatus();

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        //- 待支付和待接单状态下，用户可直接取消订单
        if (status == 1 || status ==2){
            // 更新订单状态、取消原因、取消时间
            Orders orders = Orders.builder()
                    .status(6)
                    .userId(BaseContext.getCurrentId())
                    .id(id)
                    .cancelReason("用户取消")
                    .cancelTime(LocalDateTime.now())
                    .build();

            orderMapper.update(orders);
            //- 如果在待接单状态下取消订单，需要给用户退款


        }
        if (status > 2){
            //- 商家已接单状态下，用户取消订单需电话沟通商家
            //- 派送中状态下，用户取消订单需电话沟通商家
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }


    }


    /**
     * 在来一单
     * @param id
     */
    public void repetition(Long id) {
        // 查询当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单id查询当前订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        // 将订单详情对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);

    }


    /**
     * 各个状态的订单数
     * @return
     */
    public OrderStatisticsVO statistics() {
        //待接单数量
        Integer toBeConfirmed  = orderMapper.getToBeConfirmed();
        //待派送数量
        Integer confirmed =  orderMapper.getConfirmed();
        //派送中数量
        Integer deliveryInProgress = orderMapper.getDeliveryInProgress();

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }


    /**
     * 订单搜索
     * @param
     * @return
     */
    public PageResult conditionSearch(int page,int pageSize ,Integer status) {
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setPage(page);
        ordersPageQueryDTO.setPageSize(pageSize);
        ordersPageQueryDTO.setStatus(status);


        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        //查询订单信息
        Page<Orders> ordersPage = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> orderVOList = new ArrayList<>();
        List<Orders> ordersList = ordersPage.getResult();
        if (ordersList != null && ordersList.size() >0){
            for (Orders orders : ordersList) {
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders,orderVO);

                String orderDishes = getOrderDishesStr(orders);

                // 将订单菜品信息封装到orderVO中，并添加到orderVOList
                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);

            }
        }else {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND) ;
        }


        return new PageResult(ordersPage.getTotal(),orderVOList);
    }


    private String getOrderDishesStr(Orders orders) {
        // 查询订单菜品详情信息（订单中的菜品和数量）
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        //TODO map()
        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", orderDishList);
    }


    /**
     * 接单
     * @param ordersConfirmDTO
     */
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(orders);
    }
}
