package com.pinyougou.search.listener;

import java.util.Date;

import com.google.common.collect.Maps;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.EsItem;
import com.pinyougou.pojo.Item;
import com.pinyougou.service.GoodsService;
import com.pinyougou.service.ItemSearchService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemMessageListener implements MessageListenerConcurrently {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @Reference(timeout = 10000)
    private ItemSearchService itemSearchService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        System.out.println("=======itemMessageListener接受到消息!==========");
        try {
            MessageExt messageExt = list.get(0);
            String topic = messageExt.getTopic();
            String tags = messageExt.getTags();
            String content = new String(messageExt.getBody(), "utf-8");
            List<Long> ids = JSON.parseArray(content, Long.class);
            System.out.println("ids:" + ids);
            System.out.println("tags:" + tags);
            if ("UPDATE".equals(tags)) {
                List<Item> itemList = goodsService.findItemByGoodsId(ids);
                if (itemList.size() > 0) {
                    List<EsItem> esItemList = new ArrayList<>();
                    for (Item item : itemList) {
                        EsItem esItem = new EsItem();
                        esItem.setSpec(Maps.newHashMap());
                        esItem.setId(item.getId());
                        esItem.setTitle(item.getTitle());
                        esItem.setPrice(item.getPrice().doubleValue());
                        esItem.setImage(item.getImage());
                        esItem.setGoodsId(item.getGoodsId());
                        esItem.setCategory(item.getCategory());
                        esItem.setBrand(item.getBrand());
                        esItem.setSeller(item.getSeller());
                        esItem.setUpdateTime(item.getUpdateTime());
                        Map<String, Object> spec = JSON.parseObject(item.getSpec(), Map.class);
                        esItem.setSpec(spec);

                        esItemList.add(esItem);
                    }
                    itemSearchService.saveOrUpdate(esItemList);
                }
            } else if ("DELETE".equals(tags)) {
                    itemSearchService.delete(ids);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
