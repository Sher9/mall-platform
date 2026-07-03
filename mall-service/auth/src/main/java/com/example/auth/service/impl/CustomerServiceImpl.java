package com.example.auth.service.impl;

import com.example.auth.entity.Customer;
import com.example.auth.mapper.CustomerMapper;
import com.example.auth.service.CustomerService;
import com.example.common.exception.BusinessException;
import com.example.common.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public Customer findByPhone(String phone) {
        return customerMapper.findByPhone(phone);
    }

    @Override
    @Transactional
    public Customer register(Customer customer) {
        Customer exist = findByPhone(customer.getPhone());
        if (exist != null) {
            throw new BusinessException(400, "该手机号已注册");
        }
        customer.setPassword(PasswordUtil.encode(customer.getPassword()));
        customer.setStatus(1);
        customer.setDeleted(0);
        customer.setCreateTime(LocalDateTime.now());
        customer.setUpdateTime(LocalDateTime.now());
        customerMapper.insert(customer);
        return customer;
    }
}
