package com.hxf.user.controller;

import com.hxf.param.UserCheckParam;
import com.hxf.param.UserLoginParam;
import com.hxf.pojo.User;
import com.hxf.user.service.UserService;
import com.hxf.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.net.ftp.FtpClient;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/check")
    public R check(@RequestBody @Validated UserCheckParam userCheckParam, BindingResult result) {

        boolean b = result.hasErrors();
        if (b) {
            return R.fail("账号为null,不可使用!");
        }

        return userService.check(userCheckParam);
    }

    @PostMapping("/register")
    public R register(@RequestBody @Validated User user, BindingResult result) {
        if (result.hasErrors()){
            return R.fail("参数异常,不可使用!");
        }

        return userService.register(user);
    }

    @PostMapping("login")
    public R login(@RequestBody @Validated UserLoginParam userLoginParam, BindingResult result){

        if (result.hasErrors()){
            //如果存在异常,证明请求参数不符合注解要求
            return R.fail("参数异常,不可登录!");
        }

        return userService.login(userLoginParam);
    }
}
