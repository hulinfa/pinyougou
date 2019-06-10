package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Brand;

import java.util.List;
import java.util.Map;

/**
 * BrandMapper 数据访问接口
 *
 * @version 1.0
 * @date 2019-06-08 20:18:17
 */
public interface BrandMapper extends Mapper<Brand> {

    List<Brand> findAll(Brand brand);

    @Select("select id,name as text from tb_brand")
    List<Map<String, Object>> findAllByIdAndName();
}