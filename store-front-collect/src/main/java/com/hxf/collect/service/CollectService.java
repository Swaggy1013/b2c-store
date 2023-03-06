package com.hxf.collect.service;

import com.hxf.pojo.Collect;
import com.hxf.utils.R;

public interface CollectService {
    R save(Collect collect);

    R list(Integer userId);

    R remove(Collect collect);

    R removeByPid(Integer productId);
}
