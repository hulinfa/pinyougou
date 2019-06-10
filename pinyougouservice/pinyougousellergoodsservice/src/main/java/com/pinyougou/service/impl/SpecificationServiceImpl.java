package com.pinyougou.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Specification;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.service.SpecificationService;

import java.util.List;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * SpecificationServiceImpl 服务接口实现类
 *
 * @version 1.0
 * @date 2019-06-08 20:24:31
 */
@Service(interfaceName = "com.pinyougou.service.specificationService")
@Transactional(rollbackFor = RuntimeException.class)
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;

    /**
     * 添加方法
     */
    public void save(Specification specification) {
        try {
            specificationMapper.insertSelective(specification);
            System.out.println("规格的主键id为：" + specification.getId());
            specificationOptionMapper.save(specification);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    /**
     * 修改方法
     */
    public void update(Specification specification) {
        try {
            //先批量修改specification
            specificationMapper.updateByPrimaryKey(specification);

            //批量添加规格选项
            SpecificationOption so = new SpecificationOption();
            so.setSpecId(specification.getId());
            specificationOptionMapper.delete(so);
            specificationOptionMapper.save(specification);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id删除
     */
    public void delete(Serializable id) {
        try {
            specificationMapper.deleteByPrimaryKey(id);
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
            Example example = new Example(Specification.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // 创建In条件
            criteria.andIn("id", Arrays.asList(ids));
            // 根据示范对象删除
            specificationMapper.deleteByExample(example);

            // 创建示范对象
            example = new Example(SpecificationOption.class);
            // 创建条件对象
            criteria = example.createCriteria();
            // 创建In条件
            criteria.andIn("specId", Arrays.asList(ids));

            // 根据示范对象删除
            specificationOptionMapper.deleteByExample(example);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id查询
     */
    public Specification findOne(Serializable id) {
        try {
            return specificationMapper.selectByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 查询全部
     */
    public List<Specification> findAll() {
        try {
            return specificationMapper.selectAll();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 多条件分页查询
     */
    public PageResult findByPage(Specification specification, int page, int rows) {
        try {
            PageInfo<Specification> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            specificationMapper.findAll(specification);
                        }
                    });
            return new PageResult(pageInfo.getPages(), pageInfo.getList());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<SpecificationOption> findSpecOption(Long specId) {
        try {
            SpecificationOption so = new SpecificationOption();
            so.setSpecId(specId);
            return specificationOptionMapper.select(so);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> findAllByIdAndName() {
        try {
            return specificationMapper.findAllByIdAndName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}