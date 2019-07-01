package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import com.pinyougou.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.SeckillOrderService")
@Transactional(rollbackFor = RuntimeException.class)
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    public void save(SeckillOrder seckillOrder) {

    }

    @Override
    public void update(SeckillOrder seckillOrder) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public SeckillOrder findOne(Serializable id) {
        return null;
    }

    @Override
    public List<SeckillOrder> findAll() {
        return null;
    }

    @Override
    public List<SeckillOrder> findByPage(SeckillOrder seckillOrder, int page, int rows) {
        return null;
    }

    @Override
    public synchronized void submitOrderToRedis(Long id, String userId) {
        try {
            //分布式锁，为true代表获取锁成功
            boolean lock = redisTemplate.opsForValue().setIfAbsent("kill_" + id, true);
            if (lock) {
                SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(id);
                // 判断库存数据
                if (seckillGoods != null && seckillGoods.getStockCount() > 0) {
                    // 减库存(redis)
                    seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
                    // 判断是否已经被秒光
                    if (seckillGoods.getStockCount() == 0) {
                        // 同步秒杀商品到数据库(修改库存)
                        seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                        // 删除Redis中的秒杀商品
                        redisTemplate.boundHashOps("seckillGoodsList").delete(id);
                    } else {
                        // 重新存入Redis中
                        redisTemplate.boundHashOps("seckillGoodsList").put(id, seckillGoods);
                    }
                    // 创建秒杀订单对象
                    SeckillOrder seckillOrder = new SeckillOrder();
                    // 设置订单id
                    seckillOrder.setId(idWorker.nextId());
                    // 设置秒杀商品id
                    seckillOrder.setSeckillId(id);
                    // 设置秒杀价格
                    seckillOrder.setMoney(seckillGoods.getCostPrice());
                    // 设置用户id
                    seckillOrder.setUserId(userId);
                    // 设置商家id
                    seckillOrder.setSellerId(seckillGoods.getSellerId());
                    // 设置创建时间
                    seckillOrder.setCreateTime(new Date());
                    // 设置状态码(未付款)
                    seckillOrder.setStatus("0");
                    // 保存订单到Redis
                    redisTemplate.boundHashOps("seckillOrderList").put(userId, seckillOrder);
                }
                // 释放分布式锁
                redisTemplate.delete("kill_" + id);
            } else {
                throw new RuntimeException("秒杀商品已锁！");
            }
        } catch (Exception e) {
            // 释放分布式锁(怕出现死锁)
            redisTemplate.delete("kill_" + id);
            throw new RuntimeException(e);
        }

    }

    @Override
    public SeckillOrder findOrderFromRedis(String userId) {
        try {
            // 从Redis中查询用户秒杀订单
            return (SeckillOrder) redisTemplate.boundHashOps("seckillOrderList").get(userId);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 支付成功保存订单
     *
     * @param userId         用户名
     * @param transaction_id 微信交易流水号
     */
    @Override
    public void saveOrder(String userId, String transaction_id) {
        try {
            SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("seckillOrderList").get(userId);
            if (seckillOrder != null) {
                //微信交易流水号
                seckillOrder.setTransactionId(transaction_id);
                //支付时间
                seckillOrder.setPayTime(new Date());
                //修改支付状态
                seckillOrder.setStatus("1");
                //保存到数据库
                seckillOrderMapper.insertSelective(seckillOrder);
                //删除Redis中的订单
                redisTemplate.boundHashOps("seckillOrderList").delete(userId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
