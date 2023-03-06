package com.hxf.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ProductPromoParam extends PageParam{

    @NotBlank
    public String categoryName;
}
