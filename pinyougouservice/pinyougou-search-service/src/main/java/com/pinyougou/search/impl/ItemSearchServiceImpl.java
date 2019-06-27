package com.pinyougou.search.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.EsItem;
import com.pinyougou.search.dao.EsItemDao;
import com.pinyougou.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "ItemSearchService")
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


        /* ############## 1.搜索高亮  ###############*/

        if (StringUtils.isNoneBlank(keywords)) {

            //builder.withQuery(QueryBuilders.matchQuery("keywords", keywords));

            //根据多条件匹配查询条件
            builder.withQuery(QueryBuilders.multiMatchQuery(keywords, "title", "category", "brand", "seller"));
            HighlightBuilder.Field field = new HighlightBuilder.Field("title")
                    .preTags("<font color='red'>").postTags("</font>")
                    .fragmentSize(50);//设置文本截断

            builder.withHighlightFields(field);
        }

        /* ############## 2.搜索过滤  ###############*/
        BoolQueryBuilder boolbuilder = QueryBuilders.boolQuery();

        //分类过滤
        String category = (String) params.get("category");
        if (StringUtils.isNoneBlank(category)) {
            boolbuilder.must(QueryBuilders.termQuery("category", category));
        }

        //品牌过滤
        String brand = (String) params.get("brand");
        if (StringUtils.isNoneBlank(brand)) {
            boolbuilder.must(QueryBuilders.termQuery("brand", brand));
        }

        //规格过滤
        Map<String, String> specMap = (Map<String, String>) params.get("spec");
        if (specMap != null && specMap.size() > 0) {
            for (String key : specMap.keySet()) {
                String field = "spec." + key + ".keyword";
                boolbuilder.must(QueryBuilders.nestedQuery("spec", QueryBuilders.termQuery(field, specMap.get(key)), ScoreMode.Max));
            }
        }

        //价格过滤
        String price = (String) params.get("price");
        if (StringUtils.isNoneBlank(price)) {
            String[] priceArr = price.split("-");
            RangeQueryBuilder rqRange = QueryBuilders.rangeQuery("price");
            if ("*".equals(priceArr[1])) {
                rqRange.gt(priceArr[0]);
            } else {
                rqRange.from(priceArr[0]).to(priceArr[1]);
            }
            boolbuilder.must(rqRange);
        }

        builder.withFilter(boolbuilder);

        SearchQuery query = builder.build();


        /*############# 分页查询 ############*/
        Integer currentPage = (Integer) params.get("page");
        if (currentPage == null) {
            currentPage = 1;
        }
        query.setPageable(PageRequest.of(currentPage - 1, 20));

        /*#############  排序查询  ############*/
        String sortValue = (String) params.get("sortValue");
        String sortField = (String) params.get("sortField");
        if (StringUtils.isNoneBlank(sortValue) && StringUtils.isNoneBlank(sortField)) {
            Sort sort = new Sort("ASC".equalsIgnoreCase(sortValue) ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
            query.addSort(sort);
        }

        AggregatedPage<EsItem> esItems = esTemplate.queryForPage(query, EsItem.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

                List<T> content = new ArrayList<>();

                for (SearchHit hit : searchResponse.getHits()) {
                    EsItem esItem = JSON.parseObject(hit.getSourceAsString(), EsItem.class);

                    //获取高亮标题
                    HighlightField highlightTitle = hit.getHighlightFields().get("title");

                    if (highlightTitle != null) {
                        String title = highlightTitle.getFragments()[0].toString();
                        esItem.setTitle(title);
                    }
                    content.add((T) esItem);
                }
                return new AggregatedPageImpl<T>(content, pageable, searchResponse.getHits().getTotalHits());
            }
        });

        Map<String, Object> data = new HashMap<>();

        data.put("total", esItems.getTotalElements());
        data.put("rows", esItems.getContent());
        data.put("totalPages", esItems.getTotalPages());
        return data;
    }

    @Override
    public void saveOrUpdate(List<EsItem> esItemList) {
        try {
            esItemDao.saveAll(esItemList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(List<Long> ids) {
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setIndex("pinyougou");
        deleteQuery.setType("item");
        deleteQuery.setQuery(QueryBuilders.termsQuery("goodsId", ids));
        esTemplate.delete(deleteQuery);
    }
}
