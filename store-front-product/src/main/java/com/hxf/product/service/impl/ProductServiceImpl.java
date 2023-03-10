package com.hxf.product.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxf.clients.*;
import com.hxf.param.ProductHotParam;
import com.hxf.param.ProductIdsParam;
import com.hxf.param.ProductSaveParam;
import com.hxf.param.ProductSearchParam;
import com.hxf.pojo.Picture;
import com.hxf.pojo.Product;
import com.hxf.product.mapper.PictureMapper;
import com.hxf.product.mapper.ProductMapper;
import com.hxf.product.service.ProductService;
import com.hxf.to.OrderToProduct;
import com.hxf.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper,Product> implements ProductService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SearchClient searchClient;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PictureMapper pictureMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CartClient cartClient;

    @Autowired
    private CollectClient collectClient;

    @Autowired
    private OrderClient orderClient;

    @Cacheable(value = "list.product", key = "#categoryName", cacheManager = "cacheManagerDay")
    @Override
    public R promo(String categoryName) {
        R r = categoryClient.byName(categoryName);

        if (r.getCode().equals(R.FAIL_CODE)) {
            log.info("ProductServiceImpl.promo?????????????????????:{}","??????????????????");
            return r;
        }

        LinkedHashMap<String,Object> map = (LinkedHashMap<String, Object>) r.getData();

        Integer categoryId = (Integer) map.get("category_id");

        QueryWrapper<Product> productQueryWrapper = new QueryWrapper<>();
        productQueryWrapper.eq("category_id",categoryId);
        productQueryWrapper.orderByDesc("product_sales");

        IPage<Product> page = new Page<>(1,7);
        page = productMapper.selectPage(page,productQueryWrapper);

        List<Product> productList = page.getRecords();
        long total = page.getTotal();

        log.info("ProductServiceImpl.promo????????????,??????:{}",productList);

        return R.ok("??????????????????",productList);
    }

    @Cacheable(value = "list.product", key = "#productHotParam.categoryName")
    @Override
    public R hots(ProductHotParam productHotParam) {
        R r = categoryClient.hots(productHotParam);

        if (r.getCode().equals(R.FAIL_CODE)){
            log.info("ProductServiceImpl.hots????????????,??????:{}",r.getMsg());
            return r;
        }

        List<Object> ids = (List<Object>) r.getData();

        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("category_id",ids);
        queryWrapper.orderByDesc("product_sales");

        IPage<Product> page = new Page<>(1,7);

        page = productMapper.selectPage(page,queryWrapper);

        List<Product> records = page.getRecords();

        R ok = R.ok("?????????????????????????????????!",records);
        log.info("ProductServiceImpl.hots????????????,??????:{}",ok);

        return ok;
    }

    @Override
    public R clist() {
        R r = categoryClient.list();
        log.info("ProductServiceImpl.clist????????????,??????:{}",r);

        return r;
    }

    @Cacheable(value = "list.product", key = "#productIdsParam.categoryID+'-'+#productIdsParam.currentPage+'-'+#productIdsParam.pageSize")
    @Override
    public R byCategory(ProductIdsParam productIdsParam) {
        List<Integer> categoryID = productIdsParam.getCategoryID();

        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        if (!categoryID.isEmpty()){
            queryWrapper.in("category_id",categoryID);
        }

        IPage<Product> page = new Page<>(productIdsParam.getCurrentPage(),productIdsParam.getPageSize());

        page = productMapper.selectPage(page,queryWrapper);

        R ok = R.ok("????????????!",page.getRecords(),page.getTotal());

        log.info("ProductServiceImpl.byCategory,??????:{}",ok);

        return ok;
    }

    @Cacheable(value = "product",key = "#productID")
    @Override
    public R detail(Integer productID) {
        Product product = productMapper.selectById(productID);

        R ok = R.ok(product);

        log.info("ProductServiceImpl.detail????????????,??????:{}",ok);
        return ok;
    }

    @Cacheable(value = "picture",key = "#productID")
    @Override
    public R pictures(Integer productID) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id",productID);

        List<Picture> pictureList = pictureMapper.selectList(queryWrapper);

        R ok = R.ok(pictureList);

        log.info("ProductServiceImpl.pictures????????????,??????:{}",ok);
        return ok;
    }

    @Cacheable(value = "list.category",key = "#root.methodName",cacheManager = "cacheManagerDay")
    @Override
    public List<Product> allList() {
        List<Product> productList = productMapper.selectList(null);

        log.info("ProductServiceImpl.pictures????????????,??????:{}",productList.size());
        return productList;
    }

    @Override
    public R search(ProductSearchParam productSearchParam) {
        R r = searchClient.search(productSearchParam);
        log.info("ProductServiceImpl.search????????????,??????:{}",r);
        return r;
    }

    @Cacheable(value = "list.product", key = "#productIds")
    @Override
    public R ids(List<Integer> productIds) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("product_id",productIds);

        List<Product> productList = productMapper.selectList(queryWrapper);

        R r = R.ok("????????????????????????!", productList);
        log.info("ProductServiceImpl.ids????????????,??????:{}",r);
        return r;
    }

    @Override
    public List<Product> cartList(List<Integer> productIds) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("product_id",productIds);

        List<Product> productList = productMapper.selectList(queryWrapper);
        log.info("ProductServiceImpl.cartList????????????,??????:{}",productList);
        return productList;
    }

    @Transactional
    @Override
    public void subNumber(List<OrderToProduct> orderToProducts) {
        Map<Integer, OrderToProduct> map = orderToProducts.stream().collect(Collectors.toMap(OrderToProduct::getProductId, v -> v));

        Set<Integer> productIds = map.keySet();

        List<Product> productList = productMapper.selectBatchIds(productIds);

        for (Product product : productList) {
            Integer num = map.get(product.getProductId()).getNum();
            product.setProductNum(product.getProductNum() - num);
            product.setProductSales(product.getProductSales() + num);
        }
        this.updateBatchById(productList);
        log.info("ProductServiceImpl.subNumber????????????,??????:?????????????????????????????????");
    }

    @Override
    public Long adminCount(Integer categoryId) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id",categoryId);

        Long count = productMapper.selectCount(queryWrapper);
        log.info("ProductServiceImpl.adminCount????????????,??????:{}",count);
        return count;
    }

/**
 * ??????????????????
 *   1.??????????????????
 *   2.????????????????????????
 *   3.????????????,es???????????????
 * @param productSaveParam
 * @return
 */
    @CacheEvict(value = "list.product", allEntries = true)
    @Override
    public R adminSave(ProductSaveParam productSaveParam) {

        Product product = new Product();
        //????????????
        BeanUtils.copyProperties(productSaveParam,product);

        int rows = productMapper.insert(product);

        //??????Picture????????????
        String pictures = productSaveParam.getPictures();

        if (!StringUtils.isEmpty(pictures)){
            //$ + - * | / ???^??????????????????????????????????????????????????????
            //????????????????????????[]?????????\\??????
            String[] urls = pictures.split("\\+");
            for (String url : urls) {
                Picture picture = new Picture();
                picture.setProductId(product.getProductId());
                picture.setProductPicture(url);
                //????????????????????????,????????????mybatis-plus????????????
                pictureMapper.insert(picture);
            }
        }

        searchClient.saveOrUpdate(product);

        return R.ok("????????????????????????!");
    }

    @Override
    public R adminUpdate(Product product) {
        productMapper.updateById(product);
        searchClient.saveOrUpdate(product);

        return R.ok("????????????????????????!");
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "product.list",allEntries = true),
                    @CacheEvict(value = "product", key = "#productId")
            }
    )
    @Override
    public R adminRemove(Integer productId) {
        R r = cartClient.check(productId);
        if ("004".equals(r.getCode())) {
            log.info("ProductServiceImpl.adminRemove????????????,??????:{}",r.getMsg());
        }

        r = orderClient.check(productId);
        if ("004".equals(r.getCode())) {
            log.info("ProductServiceImpl.adminRemove????????????,??????:{}",r.getMsg());
        }

        productMapper.deleteById(productId);

        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id",productId);
        pictureMapper.delete(queryWrapper);

        collectClient.remove(productId);

        searchClient.remove(productId);

        return R.ok("??????????????????");
    }
}
