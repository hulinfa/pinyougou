package com.pinyougou.service;

import com.pinyougou.pojo.Cart;

import java.util.List;

public interface CartService {

    /**
     * 添加sku商品到购物车
     * @param carts
     * @param itemId
     * @param num
     * @return
     */
    List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num);

    List<Cart> findCartRedis(String username);

    void saveCartRedis(String username, List<Cart> carts);

    List<Cart> mergeCart(List<Cart> cookieCarts, List<Cart> carts);
}
