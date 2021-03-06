package com.pinyougou.mapper;

import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.TypeTemplate;

import java.util.List;

/**
 * TypeTemplateMapper 数据访问接口
 *
 * @version 1.0
 * @date 2019-06-08 20:18:17
 */
public interface TypeTemplateMapper extends Mapper<TypeTemplate> {

    List<TypeTemplate> findAll(TypeTemplate typeTemplate);
}