package com.pinyougou.service.impl;

import com.pinyougou.pojo.Item;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.service.ItemService;
import java.util.List;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import java.io.Serializable;
import java.util.Arrays;
/**
 * ItemServiceImpl 服务接口实现类
 * @date 2019-06-08 20:24:31
 * @version 1.0
 */
public class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemMapper itemMapper;

	/** 添加方法 */
	public void save(Item item){
		try {
			itemMapper.insertSelective(item);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 修改方法 */
	public void update(Item item){
		try {
			itemMapper.updateByPrimaryKeySelective(item);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id删除 */
	public void delete(Serializable id){
		try {
			itemMapper.deleteByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 批量删除 */
	public void deleteAll(Serializable[] ids){
		try {
			// 创建示范对象
			Example example = new Example(Item.class);
			// 创建条件对象
			Example.Criteria criteria = example.createCriteria();
			// 创建In条件
			criteria.andIn("id", Arrays.asList(ids));
			// 根据示范对象删除
			itemMapper.deleteByExample(example);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id查询 */
	public Item findOne(Serializable id){
		try {
			return itemMapper.selectByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 查询全部 */
	public List<Item> findAll(){
		try {
			return itemMapper.selectAll();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 多条件分页查询 */
	public List<Item> findByPage(Item item, int page, int rows){
		try {
			PageInfo<Item> pageInfo = PageHelper.startPage(page, rows)
				.doSelectPageInfo(new ISelect() {
					@Override
					public void doSelect() {
						itemMapper.selectAll();
					}
				});
			return pageInfo.getList();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

}