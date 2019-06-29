package com.pinyougou.order.service.impl;
import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.pojo.Cart;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.service.OrderService;
import com.pinyougou.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.OrderService")
@Transactional(rollbackFor = RuntimeException.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void save(Order order) {
        try {
            // 根据用户名获取Redis中购物车数据
            List<Cart> carts = (List<Cart>) redisTemplate.boundValueOps("cart_" + order.getUserId()).get();
            // 迭代购物车数据
            for (Cart cart : carts){
                /** ####### 往订单表插入数据 ######### */
                // 生成订单主键id
                long orderId = idWorker.nextId();
                // 创建新的订单
                Order order1 = new Order();
                // 设置订单id
                order1.setOrderId(orderId);
                // 设置支付类型
                order1.setPaymentType(order.getPaymentType());
                // 设置支付状态码为“未支付”
                order1.setStatus("1");
                // 设置订单创建时间
                order1.setCreateTime(new Date());
                // 设置订单修改时间
                order1.setUpdateTime(order1.getCreateTime());
                // 设置用户名
                order1.setUserId(order.getUserId());
                // 设置收件人地址
                order1.setReceiverAreaName(order.getReceiverAreaName());
                // 设置收件人手机号码
                order1.setReceiverMobile(order.getReceiverMobile());
                // 设置收件人
                order1.setReceiver(order.getReceiver());
                // 设置订单来源
                order1.setSourceType(order.getSourceType());
                // 设置商家id
                order1.setSellerId(cart.getSellerId());

                // 定义该订单总金额
                double money = 0;
                /** ####### 往订单明细表插入数据 ######### */
                for (OrderItem orderItem : cart.getOrderItems()){
                    // 设置主键id
                    orderItem.setId(idWorker.nextId());
                    // 设置关联的订单id
                    orderItem.setOrderId(orderId);
                    // 累计总金额
                    money += orderItem.getTotalFee().doubleValue();
                    // 保存数据到订单明细表
                    orderItemMapper.insertSelective(orderItem);
                }
                // 设置支付总金额
                order1.setPayment(new BigDecimal(money));
                // 保存数据到订单表
                orderMapper.insertSelective(order1);
            }
            // 删除该用户购物车数据
            redisTemplate.delete("cart_" + order.getUserId());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Order order) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Order findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Order> findAll() {
        return null;
    }

    @Override
    public List<Order> findByPage(Order order, int page, int rows) {
        return null;
    }
}
