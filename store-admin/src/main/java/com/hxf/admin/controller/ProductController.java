package com.hxf.admin.controller;

import com.hxf.admin.service.ProductService;
import com.hxf.admin.utils.AliyunOSSUtils;
import com.hxf.param.ProductSaveParam;
import com.hxf.param.ProductSearchParam;
import com.hxf.pojo.Product;
import com.hxf.utils.R;
import com.sun.org.apache.xpath.internal.operations.Mult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private AliyunOSSUtils aliyunOSSUtils;

    @GetMapping("/list")
    public R adminList(ProductSearchParam productSearchParam){

        return productService.search(productSearchParam);
    }

    @PostMapping("/upload")
    public R adminUpload(@RequestParam("img") MultipartFile img) throws Exception {
        String filename = img.getOriginalFilename();
        filename = UUID.randomUUID().toString().replaceAll("-","");
        String contentType = img.getContentType();

        byte[] content = img.getBytes();

        int hours = 1000;

        String url = aliyunOSSUtils.uploadImage(filename, content, contentType, hours);
        System.out.println("url = " + url);
        return R.ok("图片上传成功",url);
    }

    @PostMapping("/save")
    public R adminSave(ProductSaveParam productSaveParam) {
        return productService.save(productSaveParam);
    }

    @PostMapping("/update")
    public R adminUpdate(Product product) {
        return productService.update(product);
    }

    @PostMapping("/remove")
    public R adminRemove(Integer productId) {
        return productService.remove(productId);
    }
}
