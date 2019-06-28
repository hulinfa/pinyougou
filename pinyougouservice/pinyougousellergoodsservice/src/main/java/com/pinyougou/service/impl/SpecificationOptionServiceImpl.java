package com.pinyougou.service.impl;

import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.service.SpecificationOptionService;
import java.util.List;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import java.io.Serializable;
import java.util.Arrays;
/**
 * SpecificationOptionServiceImpl 服务接口实现类
 * @date 2019-06-08 20:24:31
 * @version 1.0
 */
public class SpecificationOptionServiceImpl implements SpecificationOptionService {

	@Autowired
	private SpecificationOptionMapper specificationOptionMapper;

	/** 添加方法 */
	public void save(SpecificationOption specificationOption){
		try {
			specificationOptionMapper.insertSelective(specificationOption);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 修改方法 */
	public void update(SpecificationOption specificationOption){
		try {
			specificationOptionMapper.updateByPrimaryKeySelective(specificationOption);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id删除 */
	public void delete(Serializable id){
		try {
			specificationOptionMapper.deleteByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 批量删除 */
	public void deleteAll(Serializable[] ids){
		try {
			// 创建示范对象
			Example example = new Example(SpecificationOption.class);
			// 创建条件对象
			Example.Criteria criteria = example.createCriteria();
			// 创建In条件
			criteria.andIn("id", Arrays.asList(ids));
			// 根据示范对象删除
			specificationOptionMapper.deleteByExample(example);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id查询 */
	public SpecificationOption findOne(Serializable id){
		try {
			return specificationOptionMapper.selectByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 查询全部 */
	public List<SpecificationOption> findAll(){
		try {
			return specificationOptionMapper.selectAll();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 多条件分页查询 */
	public List<SpecificationOption> findByPage(SpecificationOption specificationOption, int page, int rows){
		try {
			PageInfo<SpecificationOption> pageInfo = PageHelper.startPage(page, rows)
				.doSelectPageInfo(new ISelect() {
					@Override
					public void doSelect() {
						specificationOptionMapper.selectAll();
					}
				});
			return pageInfo.getList();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

}