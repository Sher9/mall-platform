package com.example.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.utils.PasswordUtil;
import com.example.system.entity.User;
import com.example.system.entity.UserRole;
import com.example.system.mapper.UserMapper;
import com.example.system.mapper.UserRoleMapper;
import com.example.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, username);
        return userMapper.selectOne(query);
    }

    @Override
    public IPage<User> page(Page<User> page, String keyword, Integer status) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.like(keyword != null && !keyword.isEmpty(), User::getUsername, keyword)
             .or()
             .like(keyword != null && !keyword.isEmpty(), User::getNickname, keyword)
             .eq(status != null, User::getStatus, status)
             .eq(User::getDeleted, 0)
             .orderByDesc(User::getCreateTime);
        return userMapper.selectPage(page, query);
    }

    @Override
    public User getById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createUser(User user) {
        // 检查用户名是否已存在
        User exist = findByUsername(user.getUsername());
        if (exist != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 自动生成默认密码（123456）并用BCrypt加密
        String defaultPassword = "123456";
        user.setPassword(PasswordUtil.encode(defaultPassword));
        user.setDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        // 不更新密码
        user.setPassword(null);
        userMapper.updateById(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        User user = new User();
        user.setUserId(userId);
        user.setDeleted(1);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long userId, Integer status) {
        User user = new User();
        user.setUserId(userId);
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId) {
        String defaultPassword = "123456";
        User user = new User();
        user.setUserId(userId);
        user.setPassword(PasswordUtil.encode(defaultPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 删除旧的角色关联
        LambdaQueryWrapper<UserRole> deleteQuery = new LambdaQueryWrapper<>();
        deleteQuery.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(deleteQuery);

        // 插入新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = roleIds.stream().map(roleId -> {
                UserRole ur = new UserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                return ur;
            }).collect(Collectors.toList());
            for (UserRole ur : userRoles) {
                userRoleMapper.insert(ur);
            }
        }
    }
}
