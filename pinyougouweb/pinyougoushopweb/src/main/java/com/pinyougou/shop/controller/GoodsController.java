package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer page, @RequestParam(defaultValue = "10") Integer rows) {
        /** 获取登录商家编号 */
        String sellerId = (String) SecurityUtils.getSubject().getPrincipal();
        /** 添加查询条件 */
        goods.setSellerId(sellerId);
        /** GET请求中文转码 */
        if (StringUtils.isNoneBlank(goods.getGoodsName())) {
            try {
                goods.setGoodsName(new String(goods
                        .getGoodsName().getBytes("ISO8859-1"), "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /** 调用服务层方法查询 */
        return goodsService.findByPage(goods, page, rows);
    }

    @GetMapping("/updateMarketable")
    public boolean updateMarketable(Long[] ids, String status) {
        try {
            goodsService.updateStatus("is_marketable", ids, status);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
