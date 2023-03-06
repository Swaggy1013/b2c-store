package com.hxf.admin.service.impl;

import com.hxf.admin.service.CategoryService;
import com.hxf.clients.CategoryClient;
import com.hxf.param.PageParam;
import com.hxf.pojo.Category;
import com.hxf.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryClient categoryClient;

    @Cacheable(value = "list.category", key = "#pageParam.currentPage+'-'+#pageParam.pageSize")
    @Override
    public R listPage(PageParam pageParam) {
        R r = categoryClient.adminPageList(pageParam);
        log.info("CategoryServiceImpl.listPage业务结束，结果:{}", r);
        return r;
    }

    @CacheEvict(value = "list.category", allEntries = true)
    @Override
    public R save(Category category) {
        R r = categoryClient.adminSave(category);
        log.info("CategoryServiceImpl.save业务结束，结果:{}", r);
        return r;
    }

    @CacheEvict(value = "list.category", allEntries = true)
    @Override
    public R remove(Integer categoryId) {
        R r = categoryClient.adminRemove(categoryId);
        log.info("CategoryServiceImpl.remove业务结束，结果:{}", r);
        return r;
    }

    @CacheEvict(value = "list.category", allEntries = true)
    @Override
    public R update(Category category) {
        R r = categoryClient.adminUpdate(category);
        log.info("CategoryServiceImpl.update业务结束，结果:{}", r);
        return r;
    }
}
