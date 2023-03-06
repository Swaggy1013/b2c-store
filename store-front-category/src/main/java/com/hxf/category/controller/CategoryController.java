package com.hxf.category.controller;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.hxf.category.service.CategoryService;
import com.hxf.param.ProductHotParam;
import com.hxf.utils.R;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/promo/{categoryName}")
    public R byName(@PathVariable String categoryName) {
        if (StringUtils.isEmpty(categoryName)) {
            return R.fail("类别名称为null,无法查询类别数据");
        }

        return categoryService.byName(categoryName);
    }

    @PostMapping("/hots")
    public R hotsCategory(@RequestBody @Validated ProductHotParam productHotParam, BindingResult result) {
        if (result.hasErrors()){
            return R.fail("类别集合查询失败");
        }

        return categoryService.hotCategory(productHotParam);
    }

    @GetMapping("/list")
    public R list(){
        return categoryService.list();
    }
}
