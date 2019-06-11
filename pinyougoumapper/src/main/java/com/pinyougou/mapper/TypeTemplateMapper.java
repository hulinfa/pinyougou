package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.TypeTemplate;

import java.util.List;
import java.util.Map;

/**
 * TypeTemplateMapper 数据访问接口
 *
 * @version 1.0
 * @date 2019-06-08 20:18:17
 */
public interface TypeTemplateMapper extends Mapper<TypeTemplate> {

    List<TypeTemplate> findAll(TypeTemplate typeTemplate);

    @Select("SELECT id,name from tb_type_template order by id asc")
    List<Map<String, Object>> findTypeTemplateList();
}