package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class SeckillOrderController {

    @Reference(timeout = 10000)
    private SeckillOrderService seckillOrderService;

    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    @RequestMapping("/submitOrder")
    public boolean submitOrder(Long id, HttpServletRequest request) {
        try {
            //获取登录用户名
            String userId = request.getRemoteUser();
            seckillOrderService.submitOrderToRedis(id, userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 生成支付二维码
     */
    @GetMapping("/genPayCode")
    public Map<String, String> genPayCode(HttpServletRequest request) {

        /** 获取登录用户名 */
        String userId = request.getRemoteUser();

        /** 从Redis查询秒杀订单 */
        SeckillOrder seckillOrder = seckillOrderService.findOrderFromRedis(userId);

        /** 支付总金额（分）*/
        long totalFen = (long) (seckillOrder.getMoney().doubleValue() * 100);

        /** 调用微信支付服务接口 */
        return weixinPayService.genPayCode(seckillOrder.getId().toString(), String.valueOf(totalFen));
    }

    /*查询支付状态*/
    @GetMapping("/queryPayStatus")
    public Map<String, Integer> queryPayStatus(String outTradeNo, HttpServletRequest request) {
        //定义map集合封装数据
        Map<String, Integer> data = new HashMap<>();
        data.put("status", 2);
        Map<String, String> map = weixinPayService.queryPayStatus(outTradeNo);
        if (map != null && map.size() > 0) {
            if ("SUCCESS".equals(map.get("trade_state"))) {
                String userId = request.getRemoteUser();
                seckillOrderService.saveOrder(userId, map.get("transaction_id"));
                data.put("status", 1);
            }
        } else {
            data.put("status", 3);
        }
        return data;
    }

}
