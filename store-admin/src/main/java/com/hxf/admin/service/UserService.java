package com.hxf.admin.service;

import com.hxf.param.CartListParam;
import com.hxf.param.PageParam;
import com.hxf.pojo.User;
import com.hxf.utils.R;

public interface UserService {
    R userList(PageParam pageParam);

    R userRemove(CartListParam cartListParam);

    R userUpdate(User user);

    R save(User user);
}
