package com.hxf.cart.service;

import com.hxf.param.CartListParam;
import com.hxf.param.CartSaveParam;
import com.hxf.pojo.Cart;
import com.hxf.utils.R;

import java.util.List;

public interface CartService {
    R save(CartSaveParam cartSaveParam);


    R list(Integer userId);

    R update(Cart cart);

    R remove(Cart cart);

    void clearIds(List<Integer> cardIds);

    R removeCheck(Integer productId);
}
