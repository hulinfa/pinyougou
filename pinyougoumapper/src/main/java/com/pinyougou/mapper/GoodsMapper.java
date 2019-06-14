package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Goods;

import java.util.List;
import java.util.Map;

/**
 * GoodsMapper 数据访问接口
 *
 * @version 1.0
 * @date 2019-06-08 20:18:17
 */
public interface GoodsMapper extends Mapper<Goods> {


    List<Map<String, Object>> findAll(Goods goods);

    void updateStatus(@Param("columnName") String columnName, @Param("ids") Long[] ids, @Param("status") String status);

}