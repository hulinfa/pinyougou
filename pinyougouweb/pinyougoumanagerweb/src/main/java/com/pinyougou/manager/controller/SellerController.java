package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference(timeout = 10000)
    private SellerService sellerService;

    @GetMapping("/findByPage")
    public PageResult findByPage(Seller seller, Integer page, @RequestParam(defaultValue = "10") Integer rows) {
        try {
            seller.setName(conVertEncoding(seller.getName(), "utf-8"));
            seller.setNickName(conVertEncoding(seller.getNickName(), "utf-8"));
            seller.setLinkmanName(conVertEncoding(seller.getLinkmanName(), "utf-8"));
            return sellerService.findByPage(seller, page, rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageResult();
    }

    private String conVertEncoding(String str, String toCharSet) {
        try {
            if (StringUtils.isNoneBlank(str)) {
                return new String(str.getBytes("iso8859-1"), toCharSet);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    @RequestMapping("/updateStatus")
    public boolean updateStatus(String sellerId, String status) {
        try {
            sellerService.updateStatus(sellerId, status);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
