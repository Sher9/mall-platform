package com.example.pay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单实体
 */
@Data
@TableName("pay_order")
public class PayOrder implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 支付订单号
     */
    private String payNo;

    /**
     * 业务订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long customerId;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付类型：1-微信支付，2-支付宝支付
     */
    private Integer payType;

    /**
     * 支付状态：0-待支付，1-支付中，2-支付成功，3-支付失败，4-已取消
     */
    private Integer status;

    /**
     * 第三方支付流水号
     */
    private String transactionId;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
