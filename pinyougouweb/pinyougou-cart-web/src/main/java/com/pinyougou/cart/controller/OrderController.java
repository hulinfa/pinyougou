package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Order;
import com.pinyougou.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference(timeout = 10000)
    private OrderService orderService;

    @PostMapping("/save")
    public boolean save(@RequestBody Order order, HttpServletRequest request) {
        try {
            //获取登录用户名
            String userId = request.getRemoteUser();
            order.setUserId(userId);
            orderService.save(order);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
