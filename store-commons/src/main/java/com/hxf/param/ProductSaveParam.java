package com.hxf.param;

import com.hxf.pojo.Product;
import lombok.Data;

@Data
public class ProductSaveParam extends Product {

    private String pictures;
}
