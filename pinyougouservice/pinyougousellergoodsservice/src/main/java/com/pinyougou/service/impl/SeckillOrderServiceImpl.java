package com.pinyougou.service.impl;

import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.service.SeckillOrderService;
import java.util.List;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import java.io.Serializable;
import java.util.Arrays;
/**
 * SeckillOrderServiceImpl 服务接口实现类
 * @date 2019-06-08 20:24:31
 * @version 1.0
 */
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private SeckillOrderMapper seckillOrderMapper;

	/** 添加方法 */
	public void save(SeckillOrder seckillOrder){
		try {
			seckillOrderMapper.insertSelective(seckillOrder);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 修改方法 */
	public void update(SeckillOrder seckillOrder){
		try {
			seckillOrderMapper.updateByPrimaryKeySelective(seckillOrder);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id删除 */
	public void delete(Serializable id){
		try {
			seckillOrderMapper.deleteByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 批量删除 */
	public void deleteAll(Serializable[] ids){
		try {
			// 创建示范对象
			Example example = new Example(SeckillOrder.class);
			// 创建条件对象
			Example.Criteria criteria = example.createCriteria();
			// 创建In条件
			criteria.andIn("id", Arrays.asList(ids));
			// 根据示范对象删除
			seckillOrderMapper.deleteByExample(example);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id查询 */
	public SeckillOrder findOne(Serializable id){
		try {
			return seckillOrderMapper.selectByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 查询全部 */
	public List<SeckillOrder> findAll(){
		try {
			return seckillOrderMapper.selectAll();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 多条件分页查询 */
	public List<SeckillOrder> findByPage(SeckillOrder seckillOrder, int page, int rows){
		try {
			PageInfo<SeckillOrder> pageInfo = PageHelper.startPage(page, rows)
				.doSelectPageInfo(new ISelect() {
					@Override
					public void doSelect() {
						seckillOrderMapper.selectAll();
					}
				});
			return pageInfo.getList();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

}