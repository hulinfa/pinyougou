package com.pinyougou.item.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.service.GoodsService;
import freemarker.template.Template;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class PageMessageListener implements MessageListenerConcurrently {

    @Value("${page.dir}")
    private String pageDir;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        System.out.println("pageListener========");
        try {
            MessageExt messageExt = list.get(0);
            String content = new String(messageExt.getBody(), "utf-8");
            List<Long> goodsIds = JSON.parseArray(content, Long.class);
            System.out.println("goodsIds=======" + goodsIds);
            System.out.println("tags====" + messageExt.getTags());

            if ("CREATE".equals(messageExt.getTags())) {
                Template template = freeMarkerConfigurer.getConfiguration().getTemplate("item.ftl");
                for (Long goodsId : goodsIds) {
                    Map<String, Object> dataModel = goodsService.getGoods(Long.valueOf(goodsId));
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(pageDir + goodsId + ".html"), "utf-8");
                    template.process(dataModel, outputStreamWriter);
                    outputStreamWriter.close();
                }
            } else if ("DELETE".equals(messageExt.getTags())) {
                for (Long goodsId : goodsIds) {
                    File file = new File(pageDir + goodsId + ".html");
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
