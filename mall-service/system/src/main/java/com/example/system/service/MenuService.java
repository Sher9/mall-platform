package com.example.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.system.entity.Menu;

import java.util.List;

public interface MenuService {
    IPage<Menu> page(Page<Menu> page, String menuName);
    Menu getById(Long menuId);
    Menu save(Menu menu);
    Menu update(Menu menu);
    void delete(Long menuId);
    List<Menu> list();
    List<Menu> findMenusByRoleId(Long roleId);
}
