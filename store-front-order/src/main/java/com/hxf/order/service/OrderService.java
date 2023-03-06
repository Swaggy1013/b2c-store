package com.hxf.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxf.param.OrderParam;
import com.hxf.param.PageParam;
import com.hxf.pojo.Order;
import com.hxf.utils.R;

public interface OrderService extends IService<Order> {
    R save(OrderParam orderParam);

    R list(Integer userId);

    R removeCheck(Integer productId);

    R adminList(PageParam pageParam);
}
