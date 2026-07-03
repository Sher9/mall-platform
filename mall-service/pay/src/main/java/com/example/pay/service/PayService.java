package com.example.pay.service;

import com.example.pay.entity.PayOrder;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付服务接口
 */
public interface PayService {

    /**
     * 创建支付订单
     */
    PayOrder createPayOrder(String orderNo, Long customerId, BigDecimal amount, Integer payType);

    /**
     * 微信支付下单
     */
    Map<String, String> wechatPay(String orderNo, BigDecimal amount, String description, String openId);

    /**
     * 支付宝支付下单
     */
    String alipay(String orderNo, BigDecimal amount, String description);

    /**
     * 微信支付回调处理
     */
    boolean wechatPayCallback(Map<String, String> params);

    /**
     * 支付宝支付回调处理
     */
    boolean alipayCallback(Map<String, String> params);

    /**
     * 查询支付状态
     */
    Integer getPayStatus(String orderNo);

    /**
     * 关闭支付订单
     */
    boolean closePayOrder(String orderNo);

    /**
     * 退款
     */
    boolean refund(String orderNo, BigDecimal refundAmount, String reason);
}
