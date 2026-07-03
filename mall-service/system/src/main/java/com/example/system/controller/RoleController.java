package com.example.system.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.result.Result;
import com.example.system.entity.Menu;
import com.example.system.entity.RoleMenu;
import com.example.system.mapper.MenuMapper;
import com.example.system.mapper.RoleMenuMapper;
import com.example.system.entity.Role;
import com.example.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @GetMapping
    @SentinelResource(value = "roleList", blockHandler = "roleListBlockHandler")
    public Result<IPage<Role>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(required = false) String roleName) {
        Page<Role> page = new Page<>(pageNum, pageSize);
        IPage<Role> result = roleService.page(page, roleName);
        return Result.success(result);
    }

    @GetMapping("/{roleId}")
    public Result<Role> getById(@PathVariable Long roleId) {
        Role role = roleService.getById(roleId);
        return Result.success(role);
    }

    @PostMapping
    public Result<Role> save(@RequestBody Role role) {
        Role saved = roleService.save(role);
        return Result.success("角色创建成功", saved);
    }

    @PutMapping
    public Result<Role> update(@RequestBody Role role) {
        Role updated = roleService.update(role);
        return Result.success("角色更新成功", updated);
    }

    @DeleteMapping("/{roleId}")
    public Result<Void> delete(@PathVariable Long roleId) {
        roleService.delete(roleId);
        return Result.<Void>success("角色删除成功", null);
    }

    @GetMapping("/all")
    public Result<List<Role>> list() {
        List<Role> roles = roleService.list();
        return Result.success(roles);
    }

    /**
     * 获取角色已分配的菜单ID列表
     */
    @GetMapping("/{roleId}/menus")
    public Result<List<Long>> getRoleMenus(@PathVariable Long roleId) {
        List<Menu> menus = menuMapper.findMenusByRoleId(roleId);
        List<Long> menuIds = menus.stream().map(Menu::getMenuId).collect(Collectors.toList());
        return Result.success(menuIds);
    }

    /**
     * 分配菜单给角色
     */
    @PostMapping("/{roleId}/menus")
    public Result<Void> assignMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        // 删除旧的菜单关联
        roleMenuMapper.deleteByRoleId(roleId);
        // 插入新的菜单关联
        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenu> roleMenus = menuIds.stream().map(menuId -> {
                RoleMenu rm = new RoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                return rm;
            }).collect(Collectors.toList());
            roleMenuMapper.insertBatch(roleMenus);
        }
        return Result.<Void>success("菜单权限分配成功", null);
    }

    public Result<IPage<Role>> roleListBlockHandler(Integer pageNum, Integer pageSize, String roleName, Throwable e) {
        return Result.error(429, "请求过于频繁，请稍后重试");
    }
}
