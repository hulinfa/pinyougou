package com.pinyougou.service;

import java.util.Map;

public interface WeixinPayService {
    /**
     * 生成微信支付二维码
     * @param outTradeNo 订单交易号
     * @param totalFee 金额(分)
     * @return Map集合
     */
    Map<String, String> genPayCode(String outTradeNo, String totalFee);

    Map<String,String> queryPayStatus(String outTradeNo);

}
