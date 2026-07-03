package com.example.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.system.entity.Role;
import com.example.system.mapper.RoleMapper;
import com.example.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public IPage<Role> page(Page<Role> page, String roleName) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (roleName != null && !roleName.isEmpty()) {
            wrapper.like(Role::getRoleName, roleName);
        }
        return roleMapper.selectPage(page, wrapper);
    }

    @Override
    public Role getById(Long roleId) {
        return roleMapper.selectById(roleId);
    }

    @Override
    @Transactional
    public Role save(Role role) {
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        role.setStatus(0);
        role.setDeleted(0);
        roleMapper.insert(role);
        return role;
    }

    @Override
    @Transactional
    public Role update(Role role) {
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(role);
        return role;
    }

    @Override
    @Transactional
    public void delete(Long roleId) {
        roleMapper.deleteById(roleId);
    }

    @Override
    public List<Role> list() {
        return roleMapper.selectList(null);
    }
}
