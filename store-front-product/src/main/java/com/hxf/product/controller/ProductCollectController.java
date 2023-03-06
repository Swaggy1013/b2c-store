package com.hxf.product.controller;

import com.hxf.pojo.ProductCollectParam;
import com.hxf.product.service.ProductService;
import com.hxf.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductCollectController {

    @Autowired
    private ProductService productService;

    @PostMapping("/collect/list")
    public R productIds(@RequestBody @Validated ProductCollectParam productCollectParam, BindingResult result) {
        if (result.hasErrors()) {
            return R.fail("没有收藏数据!");
        }

        return productService.ids(productCollectParam.getProductIds());
    }
}
