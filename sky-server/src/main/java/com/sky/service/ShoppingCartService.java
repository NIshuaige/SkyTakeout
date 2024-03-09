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

    /**
     * 清空购物车
     */
    void cleanShoppingCart();

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    void deleteShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
