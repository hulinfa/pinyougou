package com.pinyougou.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.GoodsDescMapper;
import com.pinyougou.pojo.Goods;
import com.pinyougou.mapper.GoodsMapper;
import com.pinyougou.pojo.GoodsDesc;
import com.pinyougou.service.GoodsService;

import java.util.List;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;

/**
 * GoodsServiceImpl 服务接口实现类
 *
 * @version 1.0
 * @date 2019-06-08 20:24:31
 */
@Service(interfaceName = "com.pinyougou.service.GoodsService")
@Transactional(rollbackFor = RuntimeException.class)
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDescMapper goodsDescMapper;

    /**
     * 添加方法
     */
    public void save(Goods goods) {
        try {
            goods.setAuditStatus("0");
            goodsMapper.insertSelective(goods);
            GoodsDesc goodsDesc = goods.getGoodsDesc();
            goodsDesc.setGoodsId(goods.getId());
            goodsDescMapper.insertSelective(goodsDesc);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 修改方法
     */
    public void update(Goods goods) {
        try {
            goodsMapper.updateByPrimaryKeySelective(goods);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id删除
     */
    public void delete(Serializable id) {
        try {
            goodsMapper.deleteByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 批量删除
     */
    public void deleteAll(Serializable[] ids) {
        try {
            // 创建示范对象
            Example example = new Example(Goods.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // 创建In条件
            criteria.andIn("id", Arrays.asList(ids));
            // 根据示范对象删除
            goodsMapper.deleteByExample(example);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id查询
     */
    public Goods findOne(Serializable id) {
        try {
            return goodsMapper.selectByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 查询全部
     */
    public List<Goods> findAll() {
        try {
            return goodsMapper.selectAll();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 多条件分页查询
     */
    public List<Goods> findByPage(Goods goods, int page, int rows) {
        try {
            PageInfo<Goods> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            goodsMapper.selectAll();
                        }
                    });
            return pageInfo.getList();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}