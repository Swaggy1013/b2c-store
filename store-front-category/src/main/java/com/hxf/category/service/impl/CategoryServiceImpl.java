package com.hxf.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxf.category.mapper.CategoryMapper;
import com.hxf.category.service.CategoryService;
import com.hxf.clients.ProductClient;
import com.hxf.param.PageParam;
import com.hxf.param.ProductHotParam;
import com.hxf.pojo.Category;
import com.hxf.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductClient productClient;

    @Override
    public R byName(String categoryName) {
        QueryWrapper<Category> categoryQueryWrapper = new QueryWrapper<>();
        categoryQueryWrapper.eq("category_name",categoryName);
        Category category = categoryMapper.selectOne(categoryQueryWrapper);

        if (category == null) {
            log.info("CategoryServiceImpl.byName业务结束，结果:类别查询失败");
            return R.fail("类别查询失败!");
        }
        log.info("CategoryServiceImpl.byName业务结束，结果:{}","类别查询成功");
        return R.ok("类别查询成功!",category);
    }

    @Override
    public R hotCategory(ProductHotParam productHotParam) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("category_name",productHotParam.getCategoryName());
        queryWrapper.select("category_id");

        List<Object> ids = categoryMapper.selectObjs(queryWrapper);

        R ok = R.ok("类别集合查询成功",ids);
        log.info("CategoryServiceImpl.hotsCategory业务结束, 结束:{}",ok);
        return ok;
    }

    @Override
    public R list() {
        List<Category> categoryList = categoryMapper.selectList(null);
        R ok = R.ok("类别全部数据查询成功",categoryList);
        log.info("CategoryServiceImpl.list业务结束,结果:{}",ok);
        return ok;
    }

    @Override
    public R listPage(PageParam pageParam) {
        //分页参数
        IPage<Category> page = new Page<>(pageParam.getCurrentPage(),pageParam.getPageSize());
        //查询参数获取
        page = categoryMapper.selectPage(page, null);

        List<Category> records = page.getRecords();
        long total = page.getTotal();

        R r = R.ok("查询类别数据成功!", records, total);

        log.info("CategoryServiceImpl.page业务结束，结果:{}",r);

        return r;
    }

    @Override
    public R adminSave(Category category) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_name",category.getCategoryName());
        Long count = categoryMapper.selectCount(queryWrapper);

        if (count > 0) {
            return R.fail("类别已经存在,添加失败!");
        }

        int insert = categoryMapper.insert(category);
        log.info("CategoryServiceImpl.adminSave业务结束，结果:{}",insert);

        return R.ok("类别添加成功");
    }

    @Override
    public R adminRemove(Integer categoryId) {
        Long count = productClient.adminCount(categoryId);

        if (count > 0) {
            return R.fail("类别删除失败, 有:"+count+"件商品正在引用");
        }
        int i = categoryMapper.deleteById(categoryId);
        log.info("CategoryServiceImpl.adminRemove业务结束，结果:{}",i);
        return R.ok("类别数据删除成功!");
    }

    @Override
    public R adminUpdate(Category category) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_name",category.getCategoryName());
        Long count = categoryMapper.selectCount(queryWrapper);

        if (count > 0) {
            return R.fail("类别已经存在,修改失败!");
        }

        int i = categoryMapper.updateById(category);
        log.info("CategoryServiceImpl.adminUpdate业务结束，结果:{}",i);
        return R.ok("类别数据修改成功!");
    }
}
