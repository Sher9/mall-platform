package com.example.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.order.entity.OrderInfo;

import java.util.List;

public interface OrderService {
    IPage<OrderInfo> page(Page<OrderInfo> page, String orderNo);
    OrderInfo getById(Long orderId);
    OrderInfo createOrder(OrderInfo order);
    OrderInfo updateOrder(OrderInfo order);
    void delete(Long orderId);
    List<OrderInfo> list();
    List<OrderInfo> listByIds(List<Long> ids);
    OrderInfo pay(Long orderId);
    OrderInfo ship(Long orderId);
    OrderInfo receive(Long orderId);
    OrderInfo cancel(Long orderId);
}
