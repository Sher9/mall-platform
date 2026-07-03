package com.example.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.result.Result;
import com.example.system.entity.Permission;
import com.example.system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public Result<IPage<Permission>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Permission> page = new Page<>(pageNum, pageSize);
        IPage<Permission> result = permissionService.page(page, keyword);
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<Permission>> getAll() {
        List<Permission> list = permissionService.list();
        return Result.success(list);
    }

    @GetMapping("/module/{module}")
    public Result<List<Permission>> listByModule(@PathVariable String module) {
        List<Permission> list = permissionService.listByModule(module);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Permission> getById(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        return Result.success(permission);
    }

    @PostMapping
    public Result<Permission> create(@RequestBody Permission permission) {
        Permission saved = permissionService.save(permission);
        return Result.success("创建成功", saved);
    }

    @PutMapping("/{id}")
    public Result<Permission> update(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setPermissionId(id);
        Permission updated = permissionService.update(permission);
        return Result.success("更新成功", updated);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.success("删除成功");
    }

    @GetMapping("/user/{userId}")
    public Result<List<String>> getUserPermissions(@PathVariable Long userId) {
        List<String> permissions = permissionService.getUserPermissions(userId).stream().toList();
        return Result.success(permissions);
    }

    @GetMapping("/check")
    public Result<Boolean> checkPermission(
            @RequestParam Long userId,
            @RequestParam String permissionCode) {
        boolean hasPermission = permissionService.hasPermission(userId, permissionCode);
        return Result.success(hasPermission);
    }

    @PostMapping("/role/{roleId}")
    public Result<String> assignPermissionsToRole(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        permissionService.assignPermissionsToRole(roleId, permissionIds);
        return Result.success("权限分配成功");
    }

    @PostMapping("/user/{userId}")
    public Result<String> assignPermissionsToUser(
            @PathVariable Long userId,
            @RequestBody List<Long> permissionIds) {
        permissionService.assignPermissionsToUser(userId, permissionIds);
        return Result.success("权限分配成功");
    }

    @GetMapping("/role/{roleId}")
    public Result<List<String>> getRolePermissions(@PathVariable Long roleId) {
        List<String> permissions = permissionService.getRolePermissions(roleId);
        return Result.success(permissions);
    }
}
