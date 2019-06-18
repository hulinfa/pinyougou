package com.pinyougou.search.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.search.dao.EsItemDao;
import com.pinyougou.search.pojo.EsItem;
import com.pinyougou.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.ItemSearchService")
@Transactional(rollbackFor = RuntimeException.class)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private EsItemDao esItemDao;

    @Override
    public Map<String, Object> search(Map<String, Object> params) {

        String keywords = (String) params.get("keywords");
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(QueryBuilders.matchAllQuery());

        if (StringUtils.isNoneBlank(keywords)) {
            builder.withQuery(QueryBuilders.matchQuery("keywords", keywords));
        }

        SearchQuery query = builder.build();

        AggregatedPage<EsItem> esItems = esTemplate.queryForPage(query, EsItem.class);

        Map<String, Object> data = new HashMap<>();

        data.put("total", esItems.getTotalElements());
        data.put("rows", esItems.getContent());

        return data;
    }
}
