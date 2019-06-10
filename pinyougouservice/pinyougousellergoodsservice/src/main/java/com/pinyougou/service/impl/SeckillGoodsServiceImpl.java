package com.pinyougou.service.impl;

import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.service.SeckillGoodsService;
import java.util.List;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import java.io.Serializable;
import java.util.Arrays;
/**
 * SeckillGoodsServiceImpl 服务接口实现类
 * @date 2019-06-08 20:24:31
 * @version 1.0
 */
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

	@Autowired
	private SeckillGoodsMapper seckillGoodsMapper;

	/** 添加方法 */
	public void save(SeckillGoods seckillGoods){
		try {
			seckillGoodsMapper.insertSelective(seckillGoods);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 修改方法 */
	public void update(SeckillGoods seckillGoods){
		try {
			seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id删除 */
	public void delete(Serializable id){
		try {
			seckillGoodsMapper.deleteByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 批量删除 */
	public void deleteAll(Serializable[] ids){
		try {
			// 创建示范对象
			Example example = new Example(SeckillGoods.class);
			// 创建条件对象
			Example.Criteria criteria = example.createCriteria();
			// 创建In条件
			criteria.andIn("id", Arrays.asList(ids));
			// 根据示范对象删除
			seckillGoodsMapper.deleteByExample(example);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id查询 */
	public SeckillGoods findOne(Serializable id){
		try {
			return seckillGoodsMapper.selectByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 查询全部 */
	public List<SeckillGoods> findAll(){
		try {
			return seckillGoodsMapper.selectAll();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 多条件分页查询 */
	public List<SeckillGoods> findByPage(SeckillGoods seckillGoods, int page, int rows){
		try {
			PageInfo<SeckillGoods> pageInfo = PageHelper.startPage(page, rows)
				.doSelectPageInfo(new ISelect() {
					@Override
					public void doSelect() {
						seckillGoodsMapper.selectAll();
					}
				});
			return pageInfo.getList();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

}