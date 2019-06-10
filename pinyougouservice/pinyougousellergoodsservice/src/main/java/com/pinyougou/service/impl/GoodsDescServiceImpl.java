package com.pinyougou.service.impl;

import com.pinyougou.pojo.GoodsDesc;
import com.pinyougou.mapper.GoodsDescMapper;
import com.pinyougou.service.GoodsDescService;
import java.util.List;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import java.io.Serializable;
import java.util.Arrays;
/**
 * GoodsDescServiceImpl 服务接口实现类
 * @date 2019-06-08 20:24:31
 * @version 1.0
 */
public class GoodsDescServiceImpl implements GoodsDescService {

	@Autowired
	private GoodsDescMapper goodsDescMapper;

	/** 添加方法 */
	public void save(GoodsDesc goodsDesc){
		try {
			goodsDescMapper.insertSelective(goodsDesc);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 修改方法 */
	public void update(GoodsDesc goodsDesc){
		try {
			goodsDescMapper.updateByPrimaryKeySelective(goodsDesc);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id删除 */
	public void delete(Serializable id){
		try {
			goodsDescMapper.deleteByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 批量删除 */
	public void deleteAll(Serializable[] ids){
		try {
			// 创建示范对象
			Example example = new Example(GoodsDesc.class);
			// 创建条件对象
			Example.Criteria criteria = example.createCriteria();
			// 创建In条件
			criteria.andIn("id", Arrays.asList(ids));
			// 根据示范对象删除
			goodsDescMapper.deleteByExample(example);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id查询 */
	public GoodsDesc findOne(Serializable id){
		try {
			return goodsDescMapper.selectByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 查询全部 */
	public List<GoodsDesc> findAll(){
		try {
			return goodsDescMapper.selectAll();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 多条件分页查询 */
	public List<GoodsDesc> findByPage(GoodsDesc goodsDesc, int page, int rows){
		try {
			PageInfo<GoodsDesc> pageInfo = PageHelper.startPage(page, rows)
				.doSelectPageInfo(new ISelect() {
					@Override
					public void doSelect() {
						goodsDescMapper.selectAll();
					}
				});
			return pageInfo.getList();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

}