package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @RequestMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer page, @RequestParam(defaultValue = "10") Integer rows) {
        try {
            goods.setAuditStatus("0");
            String goodsName = goods.getGoodsName();
            if (StringUtils.isNoneBlank(goodsName)) {
                goods.setGoodsName(new String(goodsName.getBytes("iso-8859-1"), "utf-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return goodsService.findByPage(goods, page, rows);
    }

    @RequestMapping("/updateStatus")
    public boolean updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus("audit_status", ids, status);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/delete")
    public boolean delete(Long[] ids) {
        try {
            goodsService.updateStatus("is_delete", ids, "1");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
