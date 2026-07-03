package com.example.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("stock_info")
public class StockInfo {
    @TableId(type = IdType.AUTO)
    private Long stockId;
    private Long productId;
    private String productName;
    private Integer stockCount;
    private Integer lockedCount;
    private Integer availableCount;
    private Integer minStock;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
