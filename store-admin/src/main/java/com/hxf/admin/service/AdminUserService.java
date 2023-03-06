package com.hxf.admin.service;

import com.hxf.admin.param.AdminUserParam;
import com.hxf.admin.pojo.AdminUser;

public interface AdminUserService {
    AdminUser login(AdminUserParam adminUserParam);
}
