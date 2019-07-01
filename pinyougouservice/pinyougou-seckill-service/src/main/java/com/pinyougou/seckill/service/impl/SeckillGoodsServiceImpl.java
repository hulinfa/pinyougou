package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.SeckillGoodsService")
@Transactional
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void save(SeckillGoods seckillGoods) {

    }

    @Override
    public void update(SeckillGoods seckillGoods) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public SeckillGoods findOne(Serializable id) {
        return null;
    }

    @Override
    public List<SeckillGoods> findAll() {
        return null;
    }

    @Override
    public List<SeckillGoods> findByPage(SeckillGoods seckillGoods, int page, int rows) {
        return null;
    }

    /**
     * 查找秒杀商品的集合。
     *
     * @return
     */
    @Override
    public List<SeckillGoods> findSeckillGoods() {
        //定义秒杀商品数据。
        List<SeckillGoods> seckillGoodsList = null;
        try {
            //从redis中获取商品数据
            seckillGoodsList = redisTemplate.boundHashOps("seckillGoodsList").values();
            if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
                System.out.println("Redis缓存数据：" + seckillGoodsList);
                return seckillGoodsList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //创建示范对象
            Example example = new Example(SeckillGoods.class);

            //创建查询条件
            Example.Criteria criteria = example.createCriteria();

            //审核通过
            criteria.andEqualTo("status", "1");

            //剩余库存大于0
            criteria.andGreaterThan("stockCount", 0);

            //开始时间小于等于当前时间
            criteria.andLessThanOrEqualTo("startTime", new Date());

            //结束时间大于等于当前时间
            criteria.andGreaterThanOrEqualTo("endTime", new Date());

            //条件查询
            seckillGoodsList = seckillGoodsMapper.selectByExample(example);

            System.out.println("########### 将秒杀商品存入缓存 ############");

            try {
                for (SeckillGoods seckillGoods : seckillGoodsList) {
                    redisTemplate.boundHashOps("seckillGoodsList").put(seckillGoods.getId(), seckillGoods);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return seckillGoodsList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SeckillGoods findOneFromRedis(Long id) {
        try {
            return (SeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
