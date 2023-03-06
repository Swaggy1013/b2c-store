package com.hxf.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hxf.vo.CartVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderParam implements Serializable {

    private static final Long serialVersionUID = 1L;

    @JsonProperty("user_id")
    private Integer userId;
    private List<CartVo> products;
}
