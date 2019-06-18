package com.pinyougou.es.util;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinyougou.es.dao.EsItemDao;
import com.pinyougou.es.pojo.EsItem;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ItemImport {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private EsItemDao esItemDao;


    public void importData() {
        try {
            Item _item = new Item();
            _item.setStatus("1");
            List<Item> list = itemMapper.select(_item);
            System.out.println("已经在数据库中查到对应的数据!");

            List<EsItem> esItems = new ArrayList<>();
            for (Item item : list) {
                EsItem esItem = new EsItem();
                esItem.setId(item.getId());
                esItem.setTitle(item.getTitle());
                esItem.setPrice(item.getPrice().doubleValue());
                esItem.setImage(item.getImage());
                esItem.setGoodsId(item.getGoodsId());
                esItem.setCategory(item.getCategory());
                esItem.setBrand(item.getBrand());
                esItem.setSeller(item.getSeller());
                esItem.setUpdateTime(item.getUpdateTime());
                Map spec = new ObjectMapper().readValue(item.getSpec(), Map.class);
                esItem.setSpec(spec);

                esItems.add(esItem);
            }
            //保存所有数据
            esItemDao.saveAll(esItems);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-elasticsearch.xml");
        ItemImport bean = context.getBean(ItemImport.class);
        bean.importData();
    }
}
