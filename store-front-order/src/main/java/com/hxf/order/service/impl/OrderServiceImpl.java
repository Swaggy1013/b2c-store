package com.hxf.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxf.clients.ProductClient;
import com.hxf.order.mapper.OrderMapper;
import com.hxf.order.service.OrderService;
import com.hxf.param.OrderParam;
import com.hxf.param.PageParam;
import com.hxf.pojo.Cart;
import com.hxf.pojo.Order;
import com.hxf.pojo.Product;
import com.hxf.pojo.ProductCollectParam;
import com.hxf.utils.R;
import com.hxf.vo.AdminOrderVo;
import com.hxf.vo.CartVo;
import com.hxf.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hxf.to.OrderToProduct;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    @Override
    public R save(OrderParam orderParam) {
        List<Integer> cartIds = new ArrayList<>();
        List<OrderToProduct> orderToProducts = new ArrayList<>();
        List<Order> orderList = new ArrayList<>();

        Integer userId = orderParam.getUserId();
        Long orderId = System.currentTimeMillis();

        for (CartVo cartVo : orderParam.getProducts()) {
            cartIds.add(cartVo.getId());
            OrderToProduct orderToProduct = new OrderToProduct();
            orderToProduct.setNum(cartVo.getNum());
            orderToProduct.setProductId(cartVo.getProductID());
            orderToProducts.add(orderToProduct);

            Order order = new Order();
            order.setOrderId(orderId);
            order.setOrderTime(orderId);
            order.setUserId(userId);
            order.setProductId(cartVo.getProductID());
            order.setProductNum(cartVo.getNum());
            order.setProductPrice(cartVo.getPrice());
            orderList.add(order);
        }

        saveBatch(orderList);

        rabbitTemplate.convertAndSend("topic.ex","clear.cart",cartIds);

        rabbitTemplate.convertAndSend("topic.ex","sub.number",orderToProducts);

        return R.ok("??????????????????");
    }

    @Override
    public R list(Integer userId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<Order> list = list(queryWrapper);

        Map<Long, List<Order>> orderMap = list.stream().collect(Collectors.groupingBy(Order::getOrderId));

        List<Integer> productIds = list.stream().map(Order::getProductId).collect(Collectors.toList());

        ProductCollectParam productCollectParam = new ProductCollectParam();
        productCollectParam.setProductIds(productIds);
        List<Product> productList = productClient.cartList(productCollectParam);

        Map<Integer, Product> productMap = productList.stream().collect(Collectors.toMap(Product::getProductId, v -> v));

        List<List<OrderVo>> result = new ArrayList<>();

        for (List<Order> orders : orderMap.values()) {
            List<OrderVo> orderVos = new ArrayList<>();
            for (Order order : orders) {
                OrderVo orderVo = new OrderVo();
                BeanUtils.copyProperties(order,orderVo);
                Product product = productMap.get(order.getProductId());
                orderVo.setProductName(product.getProductName());
                orderVo.setProductPicture(product.getProductPicture());
                orderVos.add(orderVo);
            }

            result.add(orderVos);
        }

        R r = R.ok("????????????????????????!", result);
        log.info("OrderServiceImpl.list????????????,??????:{}",r);

        return r;
    }

    @Override
    public R removeCheck(Integer productId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id",productId);

        Long count = orderMapper.selectCount(queryWrapper);

        if (count > 0) {
            return R.fail("???"+count+"?????????????????????!????????????!");
        }

        return R.ok("???????????????!");
    }

    @Override
    public R adminList(PageParam pageParam) {
        //????????????????????????
        int offset = (pageParam.getCurrentPage()-1)*pageParam.getPageSize();
        int pageSize = pageParam.getPageSize();

        List<AdminOrderVo> adminOrderVoList = orderMapper.selectAdminOrder(offset,pageSize);

        return R.ok("????????????????????????!",adminOrderVoList);
    }
}
