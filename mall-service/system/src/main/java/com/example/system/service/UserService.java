package com.example.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.system.entity.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);

    /**
     * 分页查询用户列表
     */
    IPage<User> page(Page<User> page, String keyword, Integer status);

    /**
     * 根据ID查询用户
     */
    User getById(Long userId);

    /**
     * 新增用户（密码自动生成并用BCrypt加密）
     */
    User createUser(User user);

    /**
     * 更新用户
     */
    User updateUser(User user);

    /**
     * 删除用户
     */
    void deleteUser(Long userId);

    /**
     * 更新用户状态
     */
    void updateStatus(Long userId, Integer status);

    /**
     * 重置密码（自动生成默认密码并用BCrypt加密）
     */
    void resetPassword(Long userId);

    /**
     * 分配角色
     */
    void assignRoles(Long userId, List<Long> roleIds);
}
