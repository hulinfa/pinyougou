package com.pinyougou.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.mapper.ItemCatMapper;
import com.pinyougou.service.ItemCatService;

import java.util.ArrayList;
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
 * ItemCatServiceImpl 服务接口实现类
 *
 * @version 1.0
 * @date 2019-06-08 20:24:31
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatMapper itemCatMapper;

    /**
     * 添加方法
     */
    public void save(ItemCat itemCat) {
        try {
            itemCatMapper.insertSelective(itemCat);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 修改方法
     */
    public void update(ItemCat itemCat) {
        try {
            itemCatMapper.updateByPrimaryKeySelective(itemCat);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id删除
     */
    public void delete(Serializable id) {
        try {
            itemCatMapper.deleteByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 批量删除
     */
    public void deleteAll(Long[] ids) {
        try {

            List idList = new ArrayList<Long>();
            for (Long id : ids) {
                idList.add(id);
                this.findLeafNode(id, idList);
            }
            // 创建示范对象
            Example example = new Example(ItemCat.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // 创建In条件
            criteria.andIn("id", idList);
            // 根据示范对象删除
            itemCatMapper.deleteByExample(example);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void findLeafNode(Long id, List<Long> idList) {
        List<ItemCat> itemCats = this.findItemCatByParentId(id);
        for (ItemCat itemCat : itemCats) {
            Long _id = itemCat.getId();
            idList.add(_id);
            this.findLeafNode(_id, idList);
        }
    }

    /**
     * 根据主键id查询
     */
    public ItemCat findOne(Serializable id) {
        try {
            return itemCatMapper.selectByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 查询全部
     */
    public List<ItemCat> findAll() {
        try {
            return itemCatMapper.selectAll();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 多条件分页查询
     */
    public List<ItemCat> findByPage(ItemCat itemCat, int page, int rows) {
        try {
            PageInfo<ItemCat> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            itemCatMapper.selectAll();
                        }
                    });
            return pageInfo.getList();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<ItemCat> findItemCatByParentId(Long parentId) {
        try {
            ItemCat itemCat = new ItemCat();
            itemCat.setParentId(parentId);
            return itemCatMapper.select(itemCat);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}