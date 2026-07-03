package com.example.auth.controller;

import com.example.auth.dto.LoginDTO;
import com.example.auth.dto.RegisterDTO;
import com.example.auth.dto.TokenDTO;
import com.example.auth.entity.User;
import com.example.auth.service.UserService;
import com.example.common.exception.BusinessException;
import com.example.common.result.Result;
import com.example.common.utils.JwtUtil;
import com.example.common.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 管理后台认证（操作 sys_user 表）
 */
@RestController
@RequestMapping("/admin")
public class AdminAuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/login")
    public Result<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        User user = userService.findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        String encryptedPassword = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes());
        if (!user.getPassword().equals(encryptedPassword)) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (user.getStatus() != 0) {
            throw new BusinessException(401, "用户已禁用");
        }
        String token = jwtUtil.generateToken(user.getUsername());
        redisTemplate.opsForValue().set("admin:token:" + user.getUsername(), token, 24, TimeUnit.HOURS);

        // 解析角色列表
        List<String> roles = parseRoles(user.getRoles());

        return Result.success(new TokenDTO(
                token, "Bearer",
                user.getUserId(), user.getUsername(), user.getNickname(),
                null, user.getEmail(), user.getPhone(),
                roles, Collections.emptyList()
        ));
    }

    private List<String> parseRoles(String roles) {
        if (roles == null || roles.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setNickname(registerDTO.getNickname());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        User savedUser = userService.register(user);
        return Result.success("注册成功", savedUser);
    }

    @PostMapping("/logout")
    public Result<String> logout(@RequestParam String username) {
        redisTemplate.delete("admin:token:" + username);
        return Result.success("退出成功");
    }

    @GetMapping("/info")
    public Result<User> info(@RequestHeader("X-User-Name") String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }
}
