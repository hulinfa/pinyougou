package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.WeixinPayService;
import com.pinyougou.util.IdWorker;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference(timeout = 10000)
    private OrderService orderService;

    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

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

    /**
     * 生成微信支付二维码
     */
    @GetMapping("/genPayCode")
    public Map<String, String> genPayCode(HttpServletRequest request) {
        //获取登录用户名。
        String userId = request.getRemoteUser();
        //从redis获取的支付日志。
        PayLog payLog = orderService.findPayLogFromRedis(userId);
        /** 调用生成微信支付二维码服务方法 */
        return weixinPayService.genPayCode(payLog.getOutTradeNo(), "1");
    }

    @GetMapping("queryPayStatus")
    public Map<String, Integer> queryPayStatus(String outTradeNo) {
        Map<String, Integer> data = null;
        try {
            data = new HashMap<>();
            data.put("status", 3);
            //调用查询订单接口
            Map<String, String> resMap = weixinPayService.queryPayStatus(outTradeNo);
            if (resMap != null && resMap.size() > 0) {
                if ("SUCCESS".equals(resMap.get("trade_state"))) {
                    orderService.updateOrderStatus(outTradeNo,resMap.get("transaction_id"));
                    data.put("status", 1);
                }
                if ("NOTPAY".equals(resMap.get("trade_state"))) {
                    data.put("status", 2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}
