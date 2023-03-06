package com.hxf.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hxf.admin.mapper.AdminUserMapper;
import com.hxf.admin.param.AdminUserParam;
import com.hxf.admin.pojo.AdminUser;
import com.hxf.admin.service.AdminUserService;
import com.hxf.constants.UserConstants;
import com.hxf.utils.MD5Util;
import com.hxf.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Override
    public AdminUser login(AdminUserParam adminUserParam) {
        String newPwd = MD5Util.encode(adminUserParam.getUserPassword() +
                UserConstants.USER_SLAT);

        //数据库登录查询
        QueryWrapper<AdminUser> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("user_account",adminUserParam.getUserAccount());
        queryWrapper.eq("user_password",newPwd);

        AdminUser user = adminUserMapper.selectOne(queryWrapper);

        //结果封装
        log.info("AdminUserServiceImpl.login业务结束，结果:{}",user);
        return user;
    }
}
