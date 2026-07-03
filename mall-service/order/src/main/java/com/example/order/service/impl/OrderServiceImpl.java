package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.order.entity.OrderInfo;
import com.example.order.feign.ProductFeignClient;
import com.example.order.feign.StockFeignClient;
import com.example.order.mapper.OrderInfoMapper;
import com.example.order.service.OrderService;
import com.example.common.result.Result;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private StockFeignClient stockFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired(required = false)
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public IPage<OrderInfo> page(Page<OrderInfo> page, String orderNo) {
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(OrderInfo::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(OrderInfo::getCreateTime);
        return orderInfoMapper.selectPage(page, wrapper);
    }

    @Override
    public OrderInfo getById(Long orderId) {
        return orderInfoMapper.selectById(orderId);
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public OrderInfo createOrder(OrderInfo order) {
        log.info("Seata分布式事务: 创建订单, productId={}, count={}", order.getProductId(), order.getCount());

        String orderNo = "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderNo(orderNo);
        order.setStatus(0);
        order.setDeleted(0);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setTotalAmount(order.getPrice().multiply(new java.math.BigDecimal(order.getCount())));

        orderInfoMapper.insert(order);
        log.info("订单记录已创建, orderId={}", order.getOrderId());

        // 分布式事务：跨服务调用库存服务锁定库存
        Map<String, Object> params = new HashMap<>();
        params.put("productId", order.getProductId());
        params.put("productName", order.getProductName());
        params.put("minStock", 0);
        
        try {
            Result<Boolean> stockResult = stockFeignClient.increaseStock(params);
            if (stockResult == null || stockResult.getCode() != 200) {
                log.error("库存锁定失败, productId={}", order.getProductId());
                throw new RuntimeException("库存不足，创建订单失败");
            }
        } catch (Exception e) {
            log.error("调用库存服务失败: {}", e.getMessage());
            throw new RuntimeException("库存服务调用失败，订单创建回滚", e);
        }

        if (rocketMQTemplate != null) {
            rocketMQTemplate.convertAndSend("order-topic", 
                    "订单创建: " + orderNo + ", 金额: " + order.getTotalAmount());
        }
        log.info("分布式事务提交成功, orderNo={}", orderNo);
        return order;
    }

    @Override
    public OrderInfo updateOrder(OrderInfo order) {
        order.setUpdateTime(LocalDateTime.now());
        orderInfoMapper.updateById(order);
        return order;
    }

    @Override
    public void delete(Long orderId) {
        orderInfoMapper.deleteById(orderId);
    }

    @Override
    public List<OrderInfo> list() {
        return orderInfoMapper.selectList(null);
    }

    @Override
    public List<OrderInfo> listByIds(List<Long> ids) {
        return orderInfoMapper.selectBatchIds(ids);
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public OrderInfo pay(Long orderId) {
        log.info("Seata分布式事务: 支付订单, orderId={}", orderId);
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order != null && order.getStatus() == 0) {
            order.setStatus(1);
            order.setPayTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            orderInfoMapper.updateById(order);
            if (rocketMQTemplate != null) {
                rocketMQTemplate.convertAndSend("order-topic", "订单支付: " + order.getOrderNo());
            }
        }
        return order;
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public OrderInfo ship(Long orderId) {
        log.info("Seata分布式事务: 发货订单, orderId={}", orderId);
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order != null && order.getStatus() == 1) {
            order.setStatus(2);
            order.setShipTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            orderInfoMapper.updateById(order);
            if (rocketMQTemplate != null) {
                rocketMQTemplate.convertAndSend("order-topic", "订单发货: " + order.getOrderNo());
            }
        }
        return order;
    }

    @Override
    public OrderInfo receive(Long orderId) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order != null && order.getStatus() == 2) {
            order.setStatus(3);
            order.setReceiveTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            orderInfoMapper.updateById(order);
            if (rocketMQTemplate != null) {
                rocketMQTemplate.convertAndSend("order-topic", "订单完成: " + order.getOrderNo());
            }
        }
        return order;
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public OrderInfo cancel(Long orderId) {
        log.info("Seata分布式事务: 取消订单, orderId={}", orderId);
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order != null && order.getStatus() == 0) {
            order.setStatus(4);
            order.setCancelTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            orderInfoMapper.updateById(order);
            if (rocketMQTemplate != null) {
                rocketMQTemplate.convertAndSend("order-topic", "订单取消: " + order.getOrderNo());
            }
        }
        return order;
    }
}
