package com.example.system.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.result.Result;
import com.example.system.entity.Menu;
import com.example.system.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping
    @SentinelResource(value = "menuList", blockHandler = "menuListBlockHandler")
    public Result<IPage<Menu>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(required = false) String menuName) {
        Page<Menu> page = new Page<>(pageNum, pageSize);
        IPage<Menu> result = menuService.page(page, menuName);
        return Result.success(result);
    }

    @GetMapping("/{menuId}")
    public Result<Menu> getById(@PathVariable Long menuId) {
        Menu menu = menuService.getById(menuId);
        return Result.success(menu);
    }

    @PostMapping
    public Result<Menu> save(@RequestBody Menu menu) {
        Menu saved = menuService.save(menu);
        return Result.success("菜单创建成功", saved);
    }

    @PutMapping
    public Result<Menu> update(@RequestBody Menu menu) {
        Menu updated = menuService.update(menu);
        return Result.success("菜单更新成功", updated);
    }

    @DeleteMapping("/{menuId}")
    public Result<Void> delete(@PathVariable Long menuId) {
        menuService.delete(menuId);
        return Result.success("菜单删除成功", null);
    }

    @GetMapping("/all")
    public Result<List<Menu>> list() {
        List<Menu> menus = menuService.list();
        return Result.success(menus);
    }

    @GetMapping("/role/{roleId}")
    public Result<List<Menu>> getMenusByRoleId(@PathVariable Long roleId) {
        List<Menu> menus = menuService.findMenusByRoleId(roleId);
        return Result.success(menus);
    }

    public Result<IPage<Menu>> menuListBlockHandler(Integer pageNum, Integer pageSize, String menuName, Throwable e) {
        return Result.error(429, "请求过于频繁，请稍后重试");
    }
}
