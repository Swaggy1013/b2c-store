package com.hxf.user.service;

import com.hxf.pojo.Address;
import com.hxf.utils.R;

public interface AddressService {
    R list(Integer userId);

    R save(Address address);

    R remove(Integer id);
}
