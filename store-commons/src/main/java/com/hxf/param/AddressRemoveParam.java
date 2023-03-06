package com.hxf.param;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

@Data
public class AddressRemoveParam {

    @NotNull
    private Integer id;
}
