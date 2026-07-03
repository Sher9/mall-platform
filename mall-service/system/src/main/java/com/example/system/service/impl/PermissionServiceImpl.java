package com.example.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.system.entity.Permission;
import com.example.system.entity.RolePermission;
import com.example.system.entity.UserPermission;
import com.example.system.mapper.PermissionMapper;
import com.example.system.mapper.RolePermissionMapper;
import com.example.system.mapper.UserPermissionMapper;
import com.example.system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private UserPermissionMapper userPermissionMapper;

    @Override
    public IPage<Permission> page(Page<Permission> page, String keyword) {
        LambdaQueryWrapper<Permission> query = new LambdaQueryWrapper<>();
        query.eq(Permission::getStatus, 0);
        if (keyword != null && !keyword.isEmpty()) {
            query.like(Permission::getPermissionCode, keyword)
                 .or()
                 .like(Permission::getPermissionName, keyword);
        }
        return permissionMapper.selectPage(page, query);
    }

    @Override
    public Permission getById(Long permissionId) {
        return permissionMapper.selectById(permissionId);
    }

    @Override
    @Transactional
    public Permission save(Permission permission) {
        permissionMapper.insert(permission);
        return permission;
    }

    @Override
    @Transactional
    public Permission update(Permission permission) {
        permissionMapper.updateById(permission);
        return permission;
    }

    @Override
    @Transactional
    public void delete(Long permissionId) {
        permissionMapper.deleteById(permissionId);
    }

    @Override
    public List<Permission> list() {
        LambdaQueryWrapper<Permission> query = new LambdaQueryWrapper<>();
        query.eq(Permission::getStatus, 0);
        return permissionMapper.selectList(query);
    }

    @Override
    public List<Permission> listByModule(String module) {
        return permissionMapper.findByModule(module);
    }

    @Override
    public Permission findByCode(String code) {
        return permissionMapper.findByCode(code);
    }

    @Override
    public Set<String> getUserPermissions(Long userId) {
        Set<String> permissions = new HashSet<>();
        
        // 获取角色权限
        List<String> rolePermissions = permissionMapper.findPermissionCodesByUserId(userId);
        permissions.addAll(rolePermissions);
        
        // 获取直接授权
        List<String> directPermissions = permissionMapper.findDirectPermissionCodesByUserId(userId);
        permissions.addAll(directPermissions);
        
        return permissions;
    }

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        Set<String> permissions = getUserPermissions(userId);
        return permissions.contains(permissionCode);
    }

    @Override
    public boolean hasAnyPermission(Long userId, String... permissionCodes) {
        Set<String> permissions = getUserPermissions(userId);
        for (String code : permissionCodes) {
            if (permissions.contains(code)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAllPermissions(Long userId, String... permissionCodes) {
        Set<String> permissions = getUserPermissions(userId);
        for (String code : permissionCodes) {
            if (!permissions.contains(code)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        // 删除原有权限
        LambdaQueryWrapper<RolePermission> deleteQuery = new LambdaQueryWrapper<>();
        deleteQuery.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(deleteQuery);

        // 批量插入新权限
        for (Long permissionId : permissionIds) {
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            rolePermissionMapper.insert(rp);
        }
    }

    @Override
    @Transactional
    public void assignPermissionsToUser(Long userId, List<Long> permissionIds) {
        // 删除原有直接授权
        LambdaQueryWrapper<UserPermission> deleteQuery = new LambdaQueryWrapper<>();
        deleteQuery.eq(UserPermission::getUserId, userId);
        userPermissionMapper.delete(deleteQuery);

        // 批量插入新权限
        for (Long permissionId : permissionIds) {
            UserPermission up = new UserPermission();
            up.setUserId(userId);
            up.setPermissionId(permissionId);
            userPermissionMapper.insert(up);
        }
    }

    @Override
    public List<String> getRolePermissions(Long roleId) {
        return permissionMapper.findPermissionCodesByRoleId(roleId);
    }
}
