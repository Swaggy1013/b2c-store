package com.hxf.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hxf.param.ProductSearchParam;
import com.hxf.pojo.Product;
import com.hxf.utils.R;

import java.io.IOException;

public interface SearchService {
    R search(ProductSearchParam productSearchParam);

    R save(Product product) throws IOException;

    R remove(Integer productId) throws IOException;
}
