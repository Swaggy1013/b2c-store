package com.hxf.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hxf.cart.mapper.CartMapper;
import com.hxf.cart.service.CartService;
import com.hxf.clients.ProductClient;
import com.hxf.param.CartListParam;
import com.hxf.param.CartSaveParam;
import com.hxf.param.ProductIdParam;
import com.hxf.pojo.Cart;
import com.hxf.pojo.Product;
import com.hxf.pojo.ProductCollectParam;
import com.hxf.utils.R;
import com.hxf.vo.CartVo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductClient productClient;

    @Autowired
    private CartMapper cartMapper;

    @Override
    public R save(CartSaveParam cartSaveParam) {
        ProductIdParam productIdParam = new ProductIdParam();
        productIdParam.setProductID(cartSaveParam.getProductId());
        Product product = productClient.productDetail(productIdParam);

        if (product == null) {
            return R.fail("商品已经删除,无法添加到购物车");
        }
        if (product.getProductNum() == 0) {
            R ok = R.ok("没有库存数据!无法购买");
            ok.setCode("003");
            log.info("CartServiceImpl.save业务结束,结果:{}",ok);
            return ok;
        }

        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",cartSaveParam.getUserId());
        queryWrapper.eq("product_id",cartSaveParam.getProductId());
        Cart cart = cartMapper.selectOne(queryWrapper);
        if (cart != null) {
            cart.setNum(cart.getNum()+1);
            cartMapper.updateById(cart);

            R ok = R.ok("购物车存在该商品,数量+1");
            ok.setCode("002");
            log.info("CartServiceImpl.save业务结束,结果:{}",ok);
            return ok;
        }
        cart = new Cart();
        cart.setNum(1);
        cart.setProductId(cartSaveParam.getProductId());
        cart.setUserId(cartSaveParam.getUserId());
        int rows = cartMapper.insert(cart);
        log.info("CartServiceImpl.save业务结束,结果:{}",rows);

        CartVo cartVo = new CartVo(product,cart);

        return R.ok("购物车添加成功!",cartVo);
    }

    @Override
    public R list(Integer userId) {

        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<Cart> cartList = cartMapper.selectList(queryWrapper);

        if (cartList == null || cartList.size() == 0) {
            cartList = new ArrayList<Cart>();
            return R.ok("购物车空空如也!",cartList);
        }

        List<Integer> productIds = new ArrayList<>();
        for (Cart cart : cartList) {
            productIds.add(cart.getProductId());
        }
        ProductCollectParam productCollectParam = new ProductCollectParam();
        productCollectParam.setProductIds(productIds);
        List<Product> productList = productClient.cartList(productCollectParam);

        Map<Integer, Product> productMap = productList.stream().collect(Collectors.toMap(Product::getProductId, v->v));

        List<CartVo> cartVoList = new ArrayList<>();

        for (Cart cart : cartList) {
            CartVo cartVo = new CartVo(productMap.get(cart.getProductId()),cart);
            cartVoList.add(cartVo);
        }
        R r = R.ok("数据库数据查询成功!",cartVoList);
        log.info("CartServiceImpl.list业务结束,结果:{}",r);
        return r;
    }

    @Override
    public R update(Cart cart) {
        ProductIdParam productIdParam = new ProductIdParam();
        productIdParam.setProductID(cart.getProductId());
        Product product = productClient.productDetail(productIdParam);

        if (cart.getNum() > product.getProductNum()) {
            log.info("CartServiceImpl.update业务结束,结果:{}","修改失败!库存不足!");
            return R.fail("修改失败!库存不足!");
        }

        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",cart.getUserId());
        queryWrapper.eq("product_id",cart.getProductId());
        Cart newCart = cartMapper.selectOne(queryWrapper);

        newCart.setNum(cart.getNum());

        int rows = cartMapper.updateById(newCart);
        log.info("CartServiceImpl.update业务结束,结果:{}",rows);
        return R.ok("修改购物车数量成功!");
    }

    @Override
    public R remove(Cart cart) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",cart.getUserId());
        queryWrapper.eq("product_id",cart.getProductId());

        int rows = cartMapper.delete(queryWrapper);
        log.info("CartServiceImpl.remove业务结束,结果:{}",rows);
        return R.ok("删除购物车数据成功!");
    }

    @Override
    public void clearIds(List<Integer> cardIds) {

        cartMapper.deleteBatchIds(cardIds);
        log.info("CartServiceImpl.clearIds业务结束,结果:{}",cardIds);
    }

    @Override
    public R removeCheck(Integer productId) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id",productId);

        Long count = cartMapper.selectCount(queryWrapper);

        if (count > 0) {
            return R.fail("有"+count+"件购物车商品引用!删除失败!");
        }

        return R.ok("购物车无商品引用!");
    }
}
