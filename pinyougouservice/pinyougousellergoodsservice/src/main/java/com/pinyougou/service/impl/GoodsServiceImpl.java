package com.pinyougou.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.service.GoodsService;

import java.util.*;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;

/**
 * GoodsServiceImpl 服务接口实现类
 *
 * @version 1.0
 * @date 2019-06-08 20:24:31
 */
@Service(interfaceName = "GoodsService")
@Transactional(rollbackFor = RuntimeException.class)
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private ItemMapper itemMapper;

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

            /** 判断是否启用规格 */
            if ("1".equals(goods.getIsEnableSpec())) {
                /** 迭代所有的SKU具体商品集合，往SKU表插入数据 */
                for (Item item : goods.getItems()) {
                    /** 定义具体商品的标题 */
                    StringBuilder title = new StringBuilder();
                    title.append(goods.getGoodsName());
                    /** 把规格选项JSON字符串转化成Map集合 */
                    Map<String, Object> spec = JSON.parseObject(item.getSpec());
                    for (Object value : spec.values()) {
                        /** 拼接规格选项到具体商品标题 */
                        title.append(" " + value);
                    }
                    /** 设置SKU商品的标题 */
                    item.setTitle(title.toString());
                    /** 设置SKU商品其它属性 */
                    setItemInfo(item, goods);
                    itemMapper.insertSelective(item);
                }
            } else {
                /** 创建SKU具体商品对象 */
                Item item = new Item();
                /** 设置SKU商品的标题 */
                item.setTitle(goods.getGoodsName());
                /** 设置SKU商品的价格 */
                item.setPrice(goods.getPrice());
                /** 设置SKU商品库存数据 */
                item.setNum(9999);
                /** 设置SKU商品启用状态 */
                item.setStatus("1");
                /** 设置是否默认*/
                item.setIsDefault("1");
                /** 设置规格选项 */
                item.setSpec("{}");
                /** 设置SKU商品其它属性 */
                setItemInfo(item, goods);
                itemMapper.insertSelective(item);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置SKU商品信息
     */
    private void setItemInfo(Item item, Goods goods) {
        /** 设置SKU商品图片地址 */
        List<Map> imageList = JSON.parseArray(
                goods.getGoodsDesc().getItemImages(), Map.class);
        if (imageList != null && imageList.size() > 0) {
            /** 取第一张图片 */
            item.setImage((String) imageList.get(0).get("url"));
        }
        /** 设置SKU商品的分类(三级分类) */
        item.setCategoryid(goods.getCategory3Id());
        /** 设置SKU商品的创建时间 */
        item.setCreateTime(new Date());
        /** 设置SKU商品的修改时间 */
        item.setUpdateTime(item.getCreateTime());
        /** 设置SPU商品的编号 */
        item.setGoodsId(goods.getId());
        /** 设置商家编号 */
        item.setSellerId(goods.getSellerId());
        /** 设置分类名称 */
        item.setCategory(itemCatMapper
                .selectByPrimaryKey(goods.getCategory3Id()).getName());
        /** 设置品牌名称 */
        item.setBrand(brandMapper
                .selectByPrimaryKey(goods.getBrandId()).getName());
        /** 设置商家店铺名称 */
        item.setSeller(sellerMapper.selectByPrimaryKey(
                goods.getSellerId()).getNickName());
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
    public PageResult findByPage(Goods goods, int page, int rows) {
        try {
            PageInfo<Map<String, Object>> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            goodsMapper.findAll(goods);
                        }
                    });

            /** 循环查询到的商品 */
            for (Map<String, Object> map : pageInfo.getList()) {
                ItemCat itemCat1 =
                        itemCatMapper.selectByPrimaryKey(map.get("category1Id"));
                map.put("category1Name", itemCat1 != null ? itemCat1.getName() : "");
                ItemCat itemCat2 =
                        itemCatMapper.selectByPrimaryKey(map.get("category2Id"));
                map.put("category2Name", itemCat2 != null ? itemCat2.getName() : "");
                ItemCat itemCat3 =
                        itemCatMapper.selectByPrimaryKey(map.get("category3Id"));
                map.put("category3Name", itemCat3 != null ? itemCat3.getName() : "");
            }
            return new PageResult(pageInfo.getPages(), pageInfo.getList());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updateStatus(String columnName, Long[] ids, String status) {
        try {
            goodsMapper.updateStatus(columnName, ids, status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> getGoods(Long goodsId) {
        try {
            Map<String, Object> dataModel = new HashMap<>();
            Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods", goods);
            GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc", goodsDesc);

            /** 商品分类 */
            if (goods != null && goods.getCategory3Id() != null) {
                String itemCat1 = itemCatMapper
                        .selectByPrimaryKey(goods.getCategory1Id()).getName();
                String itemCat2 = itemCatMapper
                        .selectByPrimaryKey(goods.getCategory2Id()).getName();
                String itemCat3 = itemCatMapper
                        .selectByPrimaryKey(goods.getCategory3Id()).getName();
                dataModel.put("itemCat1", itemCat1);
                dataModel.put("itemCat2", itemCat2);
                dataModel.put("itemCat3", itemCat3);
            }

            //查询sku数据
            Example example = new Example(Item.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status", "1");
            criteria.andEqualTo("goodsId", goodsId);
            example.orderBy("isDefault").desc();
            List<Item> itemList = itemMapper.selectByExample(example);

            dataModel.put("itemList", JSON.toJSONString(itemList));

            return dataModel;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Item> findItemByGoodsId(List<Long> ids) {
        try {
            Example example = new Example(Item.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("goodsId", ids);
            return itemMapper.selectByExample(example);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}