package com.example.auth.controller;

import com.example.auth.dto.CustomerLoginDTO;
import com.example.auth.dto.CustomerRegisterDTO;
import com.example.auth.entity.Customer;
import com.example.auth.service.CustomerService;
import com.example.common.exception.BusinessException;
import com.example.common.result.Result;
import com.example.common.utils.JwtUtil;
import com.example.common.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 小程序客户认证（操作 customer 表）
 */
@RestController
@RequestMapping("/customer")
public class CustomerAuthController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody CustomerLoginDTO loginDTO) {
        Customer customer = customerService.findByPhone(loginDTO.getPhone());
        if (customer == null) {
            throw new BusinessException(401, "手机号或密码错误");
        }
        if (!PasswordUtil.matches(loginDTO.getPassword(), customer.getPassword())) {
            throw new BusinessException(401, "手机号或密码错误");
        }
        if (customer.getStatus() == null || customer.getStatus() != 1) {
            throw new BusinessException(401, "账号已被禁用");
        }

        String token = jwtUtil.generateToken(loginDTO.getPhone());
        redisTemplate.opsForValue().set("customer:token:" + loginDTO.getPhone(), token, 24, TimeUnit.HOURS);
        return Result.success(Map.of("token", token, "tokenType", "Bearer"));
    }

    @PostMapping("/register")
    public Result<Customer> register(@RequestBody CustomerRegisterDTO registerDTO) {
        Customer customer = new Customer();
        customer.setPhone(registerDTO.getPhone());
        customer.setPassword(registerDTO.getPassword());
        customer.setNickname(registerDTO.getNickname());
        Customer saved = customerService.register(customer);
        return Result.success("注册成功", saved);
    }

    @PostMapping("/logout")
    public Result<String> logout(@RequestBody Map<String, String> data) {
        String phone = data.get("phone");
        redisTemplate.delete("customer:token:" + phone);
        return Result.success("退出成功");
    }

    @GetMapping("/info")
    public Result<Customer> info() {
        return Result.success(null);
    }
}
