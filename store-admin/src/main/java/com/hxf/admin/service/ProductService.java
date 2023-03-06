package com.hxf.admin.service;

import com.hxf.param.ProductSaveParam;
import com.hxf.param.ProductSearchParam;
import com.hxf.pojo.Product;
import com.hxf.utils.R;

public interface ProductService {
    R search(ProductSearchParam productSearchParam);

    R save(ProductSaveParam productSaveParam);

    R update(Product product);

    R remove(Integer productId);
}
