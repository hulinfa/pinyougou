package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.ItemCatService;
import com.pinyougou.service.TypeTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference(timeout = 10000)
    private ItemCatService itemCatService;

    /**
     * 根据的父级查询商品分类
     *
     * @param parentId
     * @return
     */
    @GetMapping("/findItemCatByParentId")
    public List<ItemCat> findItemCatByParentId(Long parentId) {
        return itemCatService.findItemCatByParentId(parentId);
    }

    @PostMapping("/save")
    public boolean save(@RequestBody ItemCat itemCat) {
        try {
            itemCatService.save(itemCat);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @PostMapping("/update")
    public boolean update(@RequestBody ItemCat itemCat) {
        try {
            itemCatService.update(itemCat);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/delete")
    public boolean delete(Long[] ids) {
        try {
            itemCatService.deleteAll(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
