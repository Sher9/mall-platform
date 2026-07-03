package com.example.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("stock_log")
public class StockLog {
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;
    private Long productId;
    private String productName;
    private Integer changeType;
    private Integer changeCount;
    private Integer beforeCount;
    private Integer afterCount;
    private String orderNo;
    private String remark;
    private String operator;
    private LocalDateTime createTime;
}
