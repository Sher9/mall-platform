package com.example.auth.service.impl;

import com.example.auth.entity.User;
import com.example.auth.mapper.UserMapper;
import com.example.auth.service.UserService;
import com.example.common.exception.BusinessException;
import com.example.common.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    @Transactional
    public User register(User user) {
        if (findByUsername(user.getUsername()) != null) {
            throw new BusinessException(400, "用户名已存在");
        }
        user.setPassword(PasswordUtil.encode(user.getPassword()));
        user.setStatus(0);
        user.setDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }
}
