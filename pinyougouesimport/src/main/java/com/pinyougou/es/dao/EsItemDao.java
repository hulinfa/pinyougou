package com.pinyougou.es.dao;

import com.pinyougou.es.pojo.EsItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsItemDao extends ElasticsearchRepository<EsItem, Long> {

}
