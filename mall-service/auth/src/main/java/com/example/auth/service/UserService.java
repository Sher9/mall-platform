package com.example.auth.service;

import com.example.auth.entity.User;

public interface UserService {
    User findByUsername(String username);
    User register(User user);
}
