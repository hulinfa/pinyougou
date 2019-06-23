package com.pinyougou.service;

import com.pinyougou.pojo.EsItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /** 搜索方法 */
    Map<String,Object> search(Map<String,Object> params);

    void saveOrUpdate(List<EsItem> esItemList);

    void delete(List<Long> ids);
}
