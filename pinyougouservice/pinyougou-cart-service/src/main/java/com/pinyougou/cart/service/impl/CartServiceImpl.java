package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Cart;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = RuntimeException.class)
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 添加sku商品到购物车。
     *
     * @param carts
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num) {
        try {
            //根据sku商品id查询sku商品对象
            Item item = itemMapper.selectByPrimaryKey(itemId);
            //获取到商品的id
            String sellerId = item.getSellerId();
            //通过商家的id判断购物车集合中是否存在该商家的购物车。
            Cart cart = searchCartBySellerId(carts, sellerId);

            if (cart == null) {
                //创建购物车对象
                cart = new Cart();
                cart.setSellerId(sellerId);
                cart.setSellerName(item.getSeller());

                //创建订单明细（购物车中一个商品）
                OrderItem orderItem = createOrderItem(item, num);
                List<OrderItem> orders = new ArrayList<>();
                orders.add(orderItem);
                cart.setOrderItems(orders);

                //将新的购物车对象添加到购物车集合。
                carts.add(cart);

            } else {//购物车集合中存在该商家购物车
                OrderItem orderItem = searchOrderItemByItemId(cart.getOrderItems(), itemId);
                if (orderItem == null) {
                    orderItem = createOrderItem(item, num);
                    cart.getOrderItems().add(orderItem);
                } else {
                    orderItem.setNum(orderItem.getNum() + num);
                    Integer totalItems = orderItem.getNum();
                    orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * totalItems));
                    if (totalItems <= 0) {
                        //删除购物车中的订单明细（商品）
                        cart.getOrderItems().remove(orderItem);
                    }
                    //如果cart的orderItem订单明细为0，则删除cart。
                    if (cart.getOrderItems().size() == 0) {
                        carts.remove(cart);
                    }
                }
            }
            return carts;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Cart> findCartRedis(String username) {
        System.out.println("获取redis中购物车;" + username);
        List<Cart> carts = (List<Cart>) redisTemplate.boundValueOps("cart_" + username).get();
        if (carts == null) {
            carts = new ArrayList<>(0);
        }
        return carts;
    }

    @Override
    public void saveCartRedis(String username, List<Cart> carts) {
        System.out.println("往redis存入购物车：" + username);
        redisTemplate.boundValueOps("cart_" + username).set(carts);
    }

    @Override
    public List<Cart> mergeCart(List<Cart> cookieCarts, List<Cart> redisCarts) {
        //迭代购物车中的数据
        for (Cart cart : cookieCarts) {
            for (OrderItem orderItem : cart.getOrderItems()) {
                redisCarts = addItemToCart(redisCarts, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return redisCarts;
    }

    //判断购物车订单集合中是否有该商品。
    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItems, Long itemId) {
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getItemId().equals(itemId)) {
                return orderItem;
            }
        }
        return null;
    }

    //创建订单明细。
    private OrderItem createOrderItem(Item item, Integer num) {
        OrderItem orderItem = new OrderItem();
        orderItem.setSellerId(item.getSellerId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        //小计
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }

    private Cart searchCartBySellerId(List<Cart> carts, String sellerId) {
        for (Cart cart : carts) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }
}
