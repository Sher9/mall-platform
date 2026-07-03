package com.example.system.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long productId;
    private String productNo;
    private String productName;
    private String category;
    private String brand;
    private BigDecimal price;
    private String unit;
    private String description;
    private String imageUrl;
    private Integer sales;
    private Integer status;
    private Integer stockCount; // 库存数量，不是product表字段
    private String createTime;
    private String updateTime;
}
