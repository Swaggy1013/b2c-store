package com.hxf.admin.service;

import com.hxf.param.PageParam;
import com.hxf.pojo.Category;
import com.hxf.utils.R;

public interface CategoryService {
    R listPage(PageParam pageParam);

    R save(Category category);

    R remove(Integer categoryId);

    R update(Category category);
}
