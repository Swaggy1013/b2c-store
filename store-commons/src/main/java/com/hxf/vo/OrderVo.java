package com.hxf.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hxf.pojo.Order;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OrderVo extends Order {

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_picture")
    private String productPicture;
}
