package com.hxf.admin.controller;

import com.hxf.admin.service.UserService;
import com.hxf.param.CartListParam;
import com.hxf.param.PageParam;
import com.hxf.pojo.User;
import com.hxf.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//用户模块
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //查询所用用户
    @GetMapping("/list")
    public R userList(PageParam pageParam) {
        return userService.userList(pageParam);
    }

    @PostMapping("/remove")
    public R userRemove(CartListParam cartListParam) {
        return userService.userRemove(cartListParam);
    }

    @PostMapping("/update")
    public R userUpdate(User user) {
        return userService.userUpdate(user);
    }

    @PostMapping("/save")
    public R userSave(User user) {
        return userService.save(user);
    }
}
