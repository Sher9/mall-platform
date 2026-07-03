package com.example.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.system.entity.Permission;

import java.util.List;
import java.util.Set;

public interface PermissionService {
    
    IPage<Permission> page(Page<Permission> page, String keyword);
    
    Permission getById(Long permissionId);
    
    Permission save(Permission permission);
    
    Permission update(Permission permission);
    
    void delete(Long permissionId);
    
    List<Permission> list();
    
    List<Permission> listByModule(String module);
    
    Permission findByCode(String code);
    
    /**
     * 获取用户所有权限（角色权限 + 直接授权）
     */
    Set<String> getUserPermissions(Long userId);
    
    /**
     * 检查用户是否具有指定权限
     */
    boolean hasPermission(Long userId, String permissionCode);
    
    /**
     * 检查用户是否具有任一指定权限
     */
    boolean hasAnyPermission(Long userId, String... permissionCodes);
    
    /**
     * 检查用户是否具有所有指定权限
     */
    boolean hasAllPermissions(Long userId, String... permissionCodes);
    
    /**
     * 为角色分配权限
     */
    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);
    
    /**
     * 为用户直接授权
     */
    void assignPermissionsToUser(Long userId, List<Long> permissionIds);
    
    /**
     * 获取角色权限列表
     */
    List<String> getRolePermissions(Long roleId);
}
