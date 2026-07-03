package com.example.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.system.entity.Customer;

import java.util.List;

public interface CustomerService {
    IPage<Customer> page(Page<Customer> page, String name);
    Customer getById(Long customerId);
    Customer save(Customer customer);
    Customer update(Customer customer);
    void delete(Long customerId);
    List<Customer> list();
    List<Customer> listByIds(List<Long> ids);
}
