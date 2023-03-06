package com.hxf.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxf.pojo.Order;
import com.hxf.vo.AdminOrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    List<AdminOrderVo> selectAdminOrder(@Param("offset") int offset,@Param("pageSize") int pageSize);
}
