package com.hxf.category.service;

import com.hxf.param.PageParam;
import com.hxf.param.ProductHotParam;
import com.hxf.pojo.Category;
import com.hxf.utils.R;

public interface CategoryService {
    R byName(String categoryName);

    R hotCategory(ProductHotParam productHotParam);

    R list();

    R listPage(PageParam pageParam);

    R adminSave(Category category);

    R adminRemove(Integer categoryId);

    R adminUpdate(Category category);
}
