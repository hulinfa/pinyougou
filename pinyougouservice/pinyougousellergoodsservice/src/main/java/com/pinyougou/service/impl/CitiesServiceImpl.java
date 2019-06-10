package com.pinyougou.service.impl;

import com.pinyougou.pojo.Cities;
import com.pinyougou.mapper.CitiesMapper;
import com.pinyougou.service.CitiesService;
import java.util.List;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import java.io.Serializable;
import java.util.Arrays;
/**
 * CitiesServiceImpl 服务接口实现类
 * @date 2019-06-08 20:24:31
 * @version 1.0
 */
public class CitiesServiceImpl implements CitiesService {

	@Autowired
	private CitiesMapper citiesMapper;

	/** 添加方法 */
	public void save(Cities cities){
		try {
			citiesMapper.insertSelective(cities);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 修改方法 */
	public void update(Cities cities){
		try {
			citiesMapper.updateByPrimaryKeySelective(cities);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id删除 */
	public void delete(Serializable id){
		try {
			citiesMapper.deleteByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 批量删除 */
	public void deleteAll(Serializable[] ids){
		try {
			// 创建示范对象
			Example example = new Example(Cities.class);
			// 创建条件对象
			Example.Criteria criteria = example.createCriteria();
			// 创建In条件
			criteria.andIn("id", Arrays.asList(ids));
			// 根据示范对象删除
			citiesMapper.deleteByExample(example);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id查询 */
	public Cities findOne(Serializable id){
		try {
			return citiesMapper.selectByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 查询全部 */
	public List<Cities> findAll(){
		try {
			return citiesMapper.selectAll();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 多条件分页查询 */
	public List<Cities> findByPage(Cities cities, int page, int rows){
		try {
			PageInfo<Cities> pageInfo = PageHelper.startPage(page, rows)
				.doSelectPageInfo(new ISelect() {
					@Override
					public void doSelect() {
						citiesMapper.selectAll();
					}
				});
			return pageInfo.getList();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

}