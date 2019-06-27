package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Brand;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class BrandController {

    @Reference(timeout = 10000)
    private BrandService brandService;

    @GetMapping("/brand/findAll")
    public List<Brand> findAll() {
        System.out.println("brandService = " + brandService);
        return brandService.findAll();
    }

    @PostMapping("/brand/save")
    public Boolean save(@RequestBody Brand brand) {
        try {
            brandService.save(brand);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/brand/findByPage")
    public PageResult findByPage(Brand brand, Integer page, @RequestParam(defaultValue = "10") Integer rows) {
        try {
            if (StringUtils.isNoneBlank(brand.getName())) {
                brand.setName(new String(brand.getName().getBytes("iso8859-1"), "utf-8"));
            }
            return brandService.findByPage(brand, page, rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageResult();
    }

    @PostMapping("/brand/update")
    public Boolean update(@RequestBody Brand brand) {
        try {
            brandService.update(brand);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/brand/delete")
    public Boolean delete(Long[] ids) {
        try {
            brandService.deleteAll(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/brand/findBrandList")
    public List<Map<String, Object>> findBrandList() {
        return brandService.findAllByIdAndName();
    }

}
