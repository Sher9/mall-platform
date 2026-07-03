package com.example.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.system.entity.Role;

import java.util.List;

public interface RoleService {
    IPage<Role> page(Page<Role> page, String roleName);
    Role getById(Long roleId);
    Role save(Role role);
    Role update(Role role);
    void delete(Long roleId);
    List<Role> list();
}
