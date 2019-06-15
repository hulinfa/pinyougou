package com.pinyougou.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.Content;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.ContentService;

import java.util.List;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;

/**
 * ContentServiceImpl 服务接口实现类
 *
 * @version 1.0
 * @date 2018-08-14 00:23:07
 */
@Service(interfaceName = "com.pinyougou.service.ContentService")
@Transactional(rollbackFor = RuntimeException.class)
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加方法
     */
    public void save(Content content) {
        try {
            contentMapper.insertSelective(content);
            redisTemplate.delete("content");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 修改方法
     */
    public void update(Content content) {
        try {
            contentMapper.updateByPrimaryKeySelective(content);
            redisTemplate.delete("content");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id删除
     */
    public void delete(Serializable id) {
        try {
            contentMapper.deleteByPrimaryKey(id);
            redisTemplate.delete("content");
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
            Example example = new Example(Content.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // 创建In条件
            criteria.andIn("id", Arrays.asList(ids));
            // 根据示范对象删除
            contentMapper.deleteByExample(example);
            redisTemplate.delete("content");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id查询
     */
    public Content findOne(Serializable id) {
        try {
            return contentMapper.selectByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 查询全部
     */
    public List<Content> findAll() {
        try {
            return contentMapper.selectAll();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 多条件分页查询
     */
    public PageResult findByPage(Content content, int page, int rows) {
        try {
            PageInfo<Content> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            contentMapper.selectAll();
                        }
                    });
            return new PageResult(pageInfo.getPages(), pageInfo.getList());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Content> findContentByCategoryId(Long categoryId) {
        List<Content> contents = null;
        try {
            contents = (List<Content>) redisTemplate.boundValueOps("content").get();
            if (contents != null) {
                System.out.println("######## 在redis缓存中读取数据  ########");
                return contents;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Example example = new Example(Content.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("categoryId", categoryId);
            criteria.andEqualTo("status", "1");
            example.orderBy("sortOrder").asc();
            contents = contentMapper.selectByExample(example);
            System.out.println("======== 在数据库中查询数据 ==========");
            try {
                redisTemplate.boundValueOps("content").set(contents);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return contents;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}