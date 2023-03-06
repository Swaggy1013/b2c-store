package com.hxf.user.service;

import com.hxf.param.PageParam;
import com.hxf.param.UserCheckParam;
import com.hxf.param.UserLoginParam;
import com.hxf.pojo.User;
import com.hxf.utils.R;

public interface UserService {
    R check(UserCheckParam userCheckParam);

    R register(User user);

    R login(UserLoginParam userLoginParam);

    R listPage(PageParam pageParam);

    R remove(Integer userId);

    R update(User user);


    R save(User user);
}
