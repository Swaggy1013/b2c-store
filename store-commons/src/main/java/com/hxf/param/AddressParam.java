package com.hxf.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hxf.pojo.Address;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddressParam {

    @NotNull
    @JsonProperty("user_id")
    private Integer userId;

    private Address add;
}
