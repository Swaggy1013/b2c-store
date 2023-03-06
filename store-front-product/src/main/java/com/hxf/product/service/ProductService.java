package com.hxf.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxf.param.ProductHotParam;
import com.hxf.param.ProductIdsParam;
import com.hxf.param.ProductSaveParam;
import com.hxf.param.ProductSearchParam;
import com.hxf.pojo.Product;
import com.hxf.to.OrderToProduct;
import com.hxf.utils.R;

import java.util.List;

public interface ProductService extends IService<Product> {
    R promo(String categoryName);

    R hots(ProductHotParam productHotParam);

    R clist();

    R byCategory(ProductIdsParam productIdsParam);

    R detail(Integer productID);

    R pictures(Integer productID);

    List<Product> allList();

    R search(ProductSearchParam productSearchParam);

    R ids(List<Integer> productIds);

    List<Product> cartList(List<Integer> productIds);

    void subNumber(List<OrderToProduct> orderToProducts);

    Long adminCount(Integer categoryId);

    R adminSave(ProductSaveParam productSaveParam);

    R adminUpdate(Product product);

    R adminRemove(Integer productId);
}
