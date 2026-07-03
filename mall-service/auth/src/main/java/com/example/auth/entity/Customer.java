package com.example.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("customer")
public class Customer {
    @TableId(value = "customer_id", type = IdType.AUTO)
    private Long customerId;
    private String customerNo;
    private String phone;
    private String password;
    private String nickname;
    private String name;
    private String contactName;
    private String email;
    private String address;
    private String company;
    private Integer status;
    private Integer deleted;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
