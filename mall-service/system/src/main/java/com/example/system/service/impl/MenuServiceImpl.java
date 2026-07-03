package com.example.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.system.entity.Menu;
import com.example.system.mapper.MenuMapper;
import com.example.system.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public IPage<Menu> page(Page<Menu> page, String menuName) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        if (menuName != null && !menuName.isEmpty()) {
            wrapper.like(Menu::getMenuName, menuName);
        }
        return menuMapper.selectPage(page, wrapper);
    }

    @Override
    public Menu getById(Long menuId) {
        return menuMapper.selectById(menuId);
    }

    @Override
    @Transactional
    public Menu save(Menu menu) {
        menu.setCreateTime(LocalDateTime.now());
        menu.setUpdateTime(LocalDateTime.now());
        // 不覆盖前端传过来的 status 和 visible
        if (menu.getStatus() == null) {
            menu.setStatus(0);
        }
        if (menu.getVisible() == null) {
            menu.setVisible(0);
        }
        menuMapper.insert(menu);
        return menu;
    }

    @Override
    @Transactional
    public Menu update(Menu menu) {
        menu.setUpdateTime(LocalDateTime.now());
        menuMapper.updateById(menu);
        return menu;
    }

    @Override
    @Transactional
    public void delete(Long menuId) {
        menuMapper.deleteById(menuId);
    }

    @Override
    public List<Menu> list() {
        return menuMapper.selectList(null);
    }

    @Override
    public List<Menu> findMenusByRoleId(Long roleId) {
        return menuMapper.findMenusByRoleId(roleId);
    }
}
