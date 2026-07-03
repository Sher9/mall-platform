package com.example.auth.service;

import com.example.auth.entity.Customer;

public interface CustomerService {
    Customer findByPhone(String phone);
    Customer register(Customer customer);
}
