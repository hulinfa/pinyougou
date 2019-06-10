package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Specification;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.service.SpecificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 规格控制器
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference(timeout = 10000)
    private SpecificationService specificationService;

    @GetMapping("/findByPage")
    public PageResult findByPage(Specification specification, Integer page, @RequestParam(defaultValue = "10") Integer rows) {
        try {
            String specName = specification.getSpecName();
            if (StringUtils.isNoneBlank(specName)) {
                specification.setSpecName(new String(specName.getBytes("iso8859-1"), "utf-8"));
            }
            return specificationService.findByPage(specification, page, rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageResult();
    }

    @PostMapping("/save")
    public boolean save(@RequestBody Specification specification) {
        try {
            specificationService.save(specification);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/findSpecOption")
    public List<SpecificationOption> findSpecOption(Long specId) {
        return specificationService.findSpecOption(specId);
    }

    @PostMapping("/update")
    public boolean update(@RequestBody Specification specification) {
        try {
            specificationService.update(specification);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/delete")
    public boolean delete(Long[] ids) {
        try {
            specificationService.deleteAll(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/findSpecList")
    public List<Map<String, Object>> findSpecList() {
        return specificationService.findAllByIdAndName();
    }

}
