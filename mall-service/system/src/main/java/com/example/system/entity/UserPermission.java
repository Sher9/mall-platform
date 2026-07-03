package com.example.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user_permission")
public class UserPermission {
    private Long userId;
    private Long permissionId;
}
