package com.hxf.search.controller;

import com.hxf.param.ProductSearchParam;
import com.hxf.pojo.Product;
import com.hxf.search.service.SearchService;
import com.hxf.utils.R;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("/product")
    public R searchProduct(@RequestBody ProductSearchParam productSearchParam) {
        return searchService.search(productSearchParam);
    }

    @PostMapping("/save")
    public R saveProduct(@RequestBody Product product) throws IOException {
        return searchService.save(product);
    }

    @PostMapping("/remove")
    public R removeProduct(@RequestBody Integer productId) throws IOException {
        return searchService.remove(productId);
    }
}
