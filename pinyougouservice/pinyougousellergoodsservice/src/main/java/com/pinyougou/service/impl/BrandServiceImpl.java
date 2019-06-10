package com.pinyougou.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.Brand;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.*;

@Service(interfaceName = "com.pinyougou.service.BrandService")
@Transactional(rollbackFor = RuntimeException.class)
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public void save(Brand brand) {
        try {
            brandMapper.insertSelective(brand);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Brand brand) {
        try {
            brandMapper.updateByPrimaryKeySelective(brand);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        brandMapper.deleteByExample(example);
    }

    @Override
    public Brand findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Brand> findAll() {
        PageInfo<Brand> pageInfo = PageHelper.startPage(1, 10).doSelectPageInfo(new ISelect() {
            @Override
            public void doSelect() {
                brandMapper.selectAll();
            }
        });
        System.out.println("总记录数：" + pageInfo.getTotal());
        System.out.println("总页数：" + pageInfo.getPages());
        return pageInfo.getList();
    }

    @Override
    public PageResult findByPage(Brand brand, int page, int rows) {
        try {
            PageInfo<Object> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    brandMapper.findAll(brand);
                }
            });
            return new PageResult(pageInfo.getPages(), pageInfo.getList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> findAllByIdAndName() {
        try {
            return brandMapper.findAllByIdAndName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
