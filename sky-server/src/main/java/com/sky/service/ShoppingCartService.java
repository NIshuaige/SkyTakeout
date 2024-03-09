/**
 * @Author：乐
 * @Package：com.sky.service
 * @Project：sky-take-out
 * @name：ShoppingCartService
 * @Date：2024/3/9 0009  09:48
 * @Filename：ShoppingCartService
 */
package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     * @return
     */
    List<ShoppingCart> list();
}
