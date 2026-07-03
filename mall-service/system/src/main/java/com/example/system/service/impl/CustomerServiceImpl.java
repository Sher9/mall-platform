package com.example.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.system.entity.Customer;
import com.example.system.mapper.CustomerMapper;
import com.example.system.service.CustomerService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired(required = false)
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public IPage<Customer> page(Page<Customer> page, String name) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Customer::getName, name);
        }
        return customerMapper.selectPage(page, wrapper);
    }

    @Override
    public Customer getById(Long customerId) {
        return customerMapper.selectById(customerId);
    }

    @Override
    @Transactional
    public Customer save(Customer customer) {
        customer.setCustomerNo("CUST" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        customer.setStatus(0);
        customer.setDeleted(0);
        customer.setCreateTime(LocalDateTime.now());
        customer.setUpdateTime(LocalDateTime.now());
        customerMapper.insert(customer);
        log.info("客户创建成功, customerId={}", customer.getCustomerId());
        if (rocketMQTemplate != null) {
            rocketMQTemplate.convertAndSend("customer-topic", "客户新增: " + customer.getName());
        }
        return customer;
    }

    @Override
    @Transactional
    public Customer update(Customer customer) {
        customer.setUpdateTime(LocalDateTime.now());
        customerMapper.updateById(customer);
        log.info("客户更新成功, customerId={}", customer.getCustomerId());
        if (rocketMQTemplate != null) {
            rocketMQTemplate.convertAndSend("customer-topic", "客户更新: " + customer.getName());
        }
        return customer;
    }

    @Override
    @Transactional
    public void delete(Long customerId) {
        customerMapper.deleteById(customerId);
        log.info("客户删除成功, customerId={}", customerId);
    }

    @Override
    public List<Customer> list() {
        return customerMapper.selectList(null);
    }

    @Override
    public List<Customer> listByIds(List<Long> ids) {
        return customerMapper.selectBatchIds(ids);
    }
}
