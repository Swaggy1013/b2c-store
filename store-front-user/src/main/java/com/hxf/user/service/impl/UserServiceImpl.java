package com.hxf.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxf.constants.UserConstants;
import com.hxf.param.PageParam;
import com.hxf.param.UserCheckParam;
import com.hxf.param.UserLoginParam;
import com.hxf.pojo.User;
import com.hxf.user.mapper.UserMapper;
import com.hxf.user.service.UserService;
import com.hxf.utils.MD5Util;
import com.hxf.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public R check(UserCheckParam userCheckParam) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",userCheckParam.getUserName());
        Long total = userMapper.selectCount(queryWrapper);
        if (total == 0){
            log.info("UserServiceImpl.check业务结束, 结果:{}","账号可以使用");
            return R.ok("账号不存在,可以使用!");
        }
        log.info("UserServiceImpl.check业务结束, 结果:{}","账号不可使用");
        return R.fail("账号存在,不可使用!");
    }

    @Override
    public R register(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",user.getUserName());
        //数据库查询
        Long total = userMapper.selectCount(queryWrapper);

        if (total > 0){
            log.info("UserServiceImpl.register业务结束，结果:{}","账号存在,注册失败!");
            return R.fail("账号已经存在,不可注册!");
        }

        String newPwd = MD5Util.encode(user.getPassword() + UserConstants.USER_SLAT);
        user.setPassword(newPwd);

        //3.插入数据库数据
        int rows = userMapper.insert(user);
        //4.返回封装结果
        if (rows == 0){
            log.info("UserServiceImpl.register业务结束，结果:{}","数据插入失败!注册失败!");
            return R.fail("注册失败!请稍后再试!");
        }

        log.info("UserServiceImpl.register业务结束，结果:{}","注册成功!");

        return R.ok("注册成功!");
    }

    @Override
    public R login(UserLoginParam userLoginParam) {
        String newPwd = MD5Util.encode(userLoginParam.getPassword() + UserConstants.USER_SLAT);

        //2.数据库查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",userLoginParam.getUserName());
        queryWrapper.eq("password",newPwd);

        User user = userMapper.selectOne(queryWrapper);

        //3.结果处理
        if (user == null) {
            log.info("UserServiceImpl.login业务结束，结果:{}","账号和密码错误!");
            return R.fail("账号或者密码错误!");
        }

        log.info("UserServiceImpl.login业务结束，结果:{}","登录成功!");
        //不返回password属性!
        user.setPassword(null);
        return R.ok("登录成功!",user);
    }

    //后台管理:查询全部用户数据
    @Override
    public R listPage(PageParam pageParam) {

        IPage<User> page = new Page<>(pageParam.getCurrentPage(),pageParam.getPageSize());
        page = userMapper.selectPage(page,null);

        List<User> records = page.getRecords();
        long total = page.getTotal();

        return R.ok("用户管理查询成功",records,total);
    }


    //根据用户id删除数据
    @Override
    public R remove(Integer userId) {
        int i = userMapper.deleteById(userId);
        log.info("UserServiceImpl.remove业务结束，结果:{}",i);
        return R.ok("用户数据删除成功");
    }

    @Override
    public R update(User user) {
        //检查密码,如果和数据库一致 不需要加密! 证明密码没有修改!
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",user.getUserId());
        queryWrapper.eq("password",user.getPassword());
        Long total = userMapper.selectCount(queryWrapper);

        if (total == 0){
            //密码不同,已经修改! 新密码需要加密
            user.setPassword(MD5Util.encode(user.getPassword()+ UserConstants.USER_SLAT));
        }

        int rows = userMapper.updateById(user);

        if (rows == 0){
            return R.fail("用户修改失败!");
        }
        return R.ok("用户修改成功");
    }

    @Override
    public R save(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",user.getUserName());
        //数据库查询
        Long total = userMapper.selectCount(queryWrapper);

        if (total > 0){
            log.info("UserServiceImpl.register业务结束，结果:{}","账号存在,添加失败!");
            return R.fail("账号已经存在,不可添加!");
        }

        String newPwd = MD5Util.encode(user.getPassword() + UserConstants.USER_SLAT);
        user.setPassword(newPwd);

        //3.插入数据库数据
        int rows = userMapper.insert(user);
        //4.返回封装结果
        if (rows == 0){
            log.info("UserServiceImpl.register业务结束，结果:{}","数据插入失败!添加失败!");
            return R.fail("添加失败!请稍后再试!");
        }

        log.info("UserServiceImpl.register业务结束，结果:{}","添加成功!");

        return R.ok("添加成功!");
    }
}
