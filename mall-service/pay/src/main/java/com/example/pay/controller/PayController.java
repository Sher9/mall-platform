package com.example.pay.controller;

import com.example.common.domain.R;
import com.example.pay.entity.PayOrder;
import com.example.pay.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    public R<PayOrder> createPayOrder(
            @RequestParam String orderNo,
            @RequestParam Long customerId,
            @RequestParam BigDecimal amount,
            @RequestParam Integer payType) {
        PayOrder payOrder = payService.createPayOrder(orderNo, customerId, amount, payType);
        return R.ok(payOrder);
    }

    /**
     * 微信支付下单
     */
    @PostMapping("/wechat")
    public R<Map<String, String>> wechatPay(
            @RequestParam String orderNo,
            @RequestParam BigDecimal amount,
            @RequestParam String description,
            @RequestParam(required = false) String openId) {
        Map<String, String> result = payService.wechatPay(orderNo, amount, description, openId);
        return R.ok(result);
    }

    /**
     * 支付宝支付下单
     */
    @PostMapping("/alipay")
    public R<String> alipay(
            @RequestParam String orderNo,
            @RequestParam BigDecimal amount,
            @RequestParam String description) {
        String form = payService.alipay(orderNo, amount, description);
        return R.ok(form);
    }

    /**
     * 微信支付回调
     */
    @PostMapping("/wechat/callback")
    public String wechatPayCallback(@RequestBody Map<String, String> params) {
        boolean success = payService.wechatPayCallback(params);
        Map<String, String> response = new HashMap<>();
        if (success) {
            response.put("code", "SUCCESS");
            response.put("message", "成功");
        } else {
            response.put("code", "FAIL");
            response.put("message", "失败");
        }
        return "<xml><return_code><![CDATA[" + response.get("code") + "]]></return_code><return_msg><![CDATA[" + response.get("message") + "]]></return_msg></xml>";
    }

    /**
     * 支付宝支付回调
     */
    @PostMapping("/alipay/callback")
    public String alipayCallback(@RequestParam Map<String, String> params) {
        boolean success = payService.alipayCallback(params);
        return success ? "success" : "failure";
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/status")
    public R<Integer> getPayStatus(@RequestParam String orderNo) {
        Integer status = payService.getPayStatus(orderNo);
        return R.ok(status);
    }

    /**
     * 关闭支付订单
     */
    @PostMapping("/close")
    public R<Void> closePayOrder(@RequestParam String orderNo) {
        boolean success = payService.closePayOrder(orderNo);
        return success ? R.ok() : R.fail("关闭失败");
    }

    /**
     * 退款
     */
    @PostMapping("/refund")
    public R<Void> refund(
            @RequestParam String orderNo,
            @RequestParam BigDecimal refundAmount,
            @RequestParam(required = false) String reason) {
        boolean success = payService.refund(orderNo, refundAmount, reason);
        return success ? R.ok() : R.fail("退款失败");
    }
}
