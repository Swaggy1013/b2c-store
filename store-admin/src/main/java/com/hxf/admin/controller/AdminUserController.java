package com.hxf.admin.controller;

import com.hxf.admin.param.AdminUserParam;
import com.hxf.admin.pojo.AdminUser;
import com.hxf.admin.service.AdminUserService;
import com.hxf.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/user")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    /**
     * 后台登录功能实现
     * @param adminUserParam
     * @return
     */
    @PostMapping("/login")
    public R login(@Validated AdminUserParam adminUserParam, BindingResult result, HttpSession session){

        //参数校验
        if (result.hasErrors()) {
            log.info("AdminUserController.login业务,参数异常!");
            return R.fail("核心参数为null,登录失败!");
        }
        //校验验证码
        String varCode = adminUserParam.getVerCode();
        String captcha = (String) session.getAttribute("captcha");
        if (!varCode.equalsIgnoreCase(captcha)){
            return R.fail("登录失败,验证码错误!");
        }
        //验证码通过验证数据
        AdminUser user = adminUserService.login(adminUserParam);

        if (user == null) {
            return R.fail("登录失败!账号或密码错误!");
        }

        //获取数据存储到session共享域,后期登录访问判断
        //存储到session共享域  key = userInfo 其他页面固定读取
        session.setAttribute("userInfo",user);
        return R.ok("登录成功!");
    }

    @GetMapping("/logout")
    public R logout(HttpSession session) {
        session.invalidate();

        return R.ok("退出登录成功!");
    }
}