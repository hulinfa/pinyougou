package com.pinyougou.search.dao;

import com.pinyougou.search.pojo.EsItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsItemDao extends ElasticsearchRepository<EsItem, Long> {

}
