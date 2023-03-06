package com.hxf.admin.controller;

import com.hxf.admin.service.CategoryService;
import com.hxf.param.PageParam;
import com.hxf.pojo.Category;
import com.hxf.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public R pageList(PageParam pageParam){
        return categoryService.listPage(pageParam);
    }

    @PostMapping("/save")
    public R save(Category category){
        return categoryService.save(category);
    }

    @PostMapping("/remove")
    public R remove(Integer categoryId){
        return categoryService.remove(categoryId);
    }

    @PostMapping("/update")
    public R update(Category category){
        return categoryService.update(category);
    }
}
