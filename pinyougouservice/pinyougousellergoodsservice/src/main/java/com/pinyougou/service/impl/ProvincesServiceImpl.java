package com.pinyougou.service.impl;

import com.pinyougou.pojo.Provinces;
import com.pinyougou.mapper.ProvincesMapper;
import com.pinyougou.service.ProvincesService;
import java.util.List;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import java.io.Serializable;
import java.util.Arrays;
/**
 * ProvincesServiceImpl 服务接口实现类
 * @date 2019-06-08 20:24:31
 * @version 1.0
 */
public class ProvincesServiceImpl implements ProvincesService {

	@Autowired
	private ProvincesMapper provincesMapper;

	/** 添加方法 */
	public void save(Provinces provinces){
		try {
			provincesMapper.insertSelective(provinces);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 修改方法 */
	public void update(Provinces provinces){
		try {
			provincesMapper.updateByPrimaryKeySelective(provinces);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id删除 */
	public void delete(Serializable id){
		try {
			provincesMapper.deleteByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 批量删除 */
	public void deleteAll(Serializable[] ids){
		try {
			// 创建示范对象
			Example example = new Example(Provinces.class);
			// 创建条件对象
			Example.Criteria criteria = example.createCriteria();
			// 创建In条件
			criteria.andIn("id", Arrays.asList(ids));
			// 根据示范对象删除
			provincesMapper.deleteByExample(example);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id查询 */
	public Provinces findOne(Serializable id){
		try {
			return provincesMapper.selectByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 查询全部 */
	public List<Provinces> findAll(){
		try {
			return provincesMapper.selectAll();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 多条件分页查询 */
	public List<Provinces> findByPage(Provinces provinces, int page, int rows){
		try {
			PageInfo<Provinces> pageInfo = PageHelper.startPage(page, rows)
				.doSelectPageInfo(new ISelect() {
					@Override
					public void doSelect() {
						provincesMapper.selectAll();
					}
				});
			return pageInfo.getList();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

}