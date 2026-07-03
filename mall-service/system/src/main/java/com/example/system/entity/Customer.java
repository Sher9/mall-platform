package com.example.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("customer")
public class Customer {
    private Long customerId;
    private String customerNo;
    private String name;
    private String contactName;
    private String phone;
    private String email;
    private String address;
    private String company;
    private Integer status;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
