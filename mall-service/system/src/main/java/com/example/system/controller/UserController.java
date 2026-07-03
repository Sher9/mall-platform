package com.example.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.result.Result;
import com.example.system.entity.User;
import com.example.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据用户名查询用户信息（供内部服务调用）
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestParam String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping
    public Result<IPage<User>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        IPage<User> result = userService.page(page, keyword, status);
        // 隐藏密码
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(result);
    }

    /**
     * 根据ID查询用户详情
     */
    @GetMapping("/{userId}")
    public Result<User> getById(@PathVariable Long userId) {
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        User saved = userService.createUser(user);
        saved.setPassword(null);
        return Result.success("用户创建成功，默认密码为123456", saved);
    }

    /**
     * 更新用户
     */
    @PutMapping
    public Result<User> updateUser(@RequestBody User user) {
        User updated = userService.updateUser(user);
        updated.setPassword(null);
        return Result.success("用户更新成功", updated);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return Result.<Void>success("用户删除成功", null);
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long userId, @RequestParam Integer status) {
        userService.updateStatus(userId, status);
        return Result.<Void>success("状态更新成功", null);
    }

    /**
     * 重置密码
     */
    @PutMapping("/reset-password")
    public Result<Void> resetPassword(@RequestParam Long userId) {
        userService.resetPassword(userId);
        return Result.<Void>success("密码重置成功，新密码为123456", null);
    }

    /**
     * 分配角色
     */
    @PutMapping("/roles")
    public Result<Void> assignRoles(@RequestParam Long userId, @RequestBody List<Long> roleIds) {
        userService.assignRoles(userId, roleIds);
        return Result.<Void>success("角色分配成功", null);
    }
}
