package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @RequestMapping("/save")
    public Boolean save(@RequestBody Goods goods) {
        try {
            String sellerId = SecurityUtils.getSubject().getPrincipal().toString();
            goods.setSellerId(sellerId);
            goodsService.save(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
