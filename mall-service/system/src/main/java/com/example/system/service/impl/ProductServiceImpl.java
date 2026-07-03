package com.example.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.system.entity.Product;
import com.example.system.entity.StockInfo;
import com.example.system.mapper.ProductMapper;
import com.example.system.mapper.StockInfoMapper;
import com.example.system.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StockInfoMapper stockInfoMapper;

    @Autowired(required = false)
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public IPage<Product> page(Page<Product> page, String productName) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (productName != null && !productName.isEmpty()) {
            wrapper.like(Product::getProductName, productName);
        }
        return productMapper.selectPage(page, wrapper);
    }

    @Override
    public Product getById(Long productId) {
        return productMapper.selectById(productId);
    }

    @Override
    @Transactional
    public Product save(Product product, Integer stockCount) {
        // 设置商品默认值和审计字段
        product.setProductNo("PROD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        product.setStatus(0);
        product.setDeleted(0);
        product.setSales(0);  // 销量默认为0
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        
        // 保存商品
        productMapper.insert(product);
        log.info("产品创建成功, productId={}", product.getProductId());
        
        // 同时插入库存数据
        StockInfo stockInfo = new StockInfo();
        stockInfo.setProductId(product.getProductId());
        stockInfo.setProductName(product.getProductName());
        stockInfo.setStockCount(stockCount != null ? stockCount : 0);
        stockInfo.setLockedCount(0);
        stockInfo.setAvailableCount(stockCount != null ? stockCount : 0);
        stockInfo.setMinStock(0);
        stockInfo.setStatus(0);
        stockInfo.setCreateTime(LocalDateTime.now());
        stockInfo.setUpdateTime(LocalDateTime.now());
        stockInfoMapper.insert(stockInfo);
        log.info("库存数据创建成功, stockId={}", stockInfo.getStockId());
        
        // 发送MQ消息
        if (rocketMQTemplate != null) {
            rocketMQTemplate.convertAndSend("product-topic", "产品新增: " + product.getProductName());
        }
        
        return product;
    }

    @Override
    @Transactional
    public Product update(Product product) {
        // 只更新商品名称和状态
        Product existingProduct = productMapper.selectById(product.getProductId());
        if (existingProduct == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 更新商品名称
        if (product.getProductName() != null) {
            existingProduct.setProductName(product.getProductName());
        }
        //更新品牌名称
        if (product.getBrand() != null) {
            existingProduct.setBrand(product.getBrand());
        }

        //更新图片地址
        if (product.getImageUrl() != null && !product.getImageUrl().equals(existingProduct.getImageUrl())) {
            existingProduct.setImageUrl(product.getImageUrl());
        }

        // 更新状态
        if (product.getStatus() != null) {
            existingProduct.setStatus(product.getStatus());
            
            // 如果产品被下架（status=1），库存状态也要改为缺货（status=1）
            if (product.getStatus() == 1) {
                StockInfo stockInfo = stockInfoMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StockInfo>()
                        .eq(StockInfo::getProductId, product.getProductId())
                );
                if (stockInfo != null) {
                    stockInfo.setStatus(1); // 缺货状态
                    stockInfo.setUpdateTime(LocalDateTime.now());
                    stockInfoMapper.updateById(stockInfo);
                    log.info("库存状态已更新为缺货, productId={}", product.getProductId());
                }
            }
        }
        
        existingProduct.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(existingProduct);
        log.info("产品更新成功, productId={}", product.getProductId());
        return existingProduct;
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        // 不删除商品，只修改库存状态为缺货
        StockInfo stockInfo = stockInfoMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StockInfo>()
                .eq(StockInfo::getProductId, productId)
        );
        
        if (stockInfo != null) {
            stockInfo.setStatus(1); // 缺货状态
            stockInfo.setUpdateTime(LocalDateTime.now());
            stockInfoMapper.updateById(stockInfo);
            log.info("库存状态已更新为缺货(删除商品), productId={}", productId);
        } else {
            log.warn("未找到对应的库存记录, productId={}", productId);
        }
        
        // 可选：逻辑删除商品（设置deleted=1）
        Product product = productMapper.selectById(productId);
        if (product != null) {
            product.setDeleted(1);
            product.setUpdateTime(LocalDateTime.now());
            productMapper.updateById(product);
            log.info("商品已逻辑删除, productId={}", productId);
        }
    }

    @Override
    public List<Product> list() {
        return productMapper.selectList(null);
    }

    @Override
    public List<Product> getHotProducts(int limit) {
        // 按销量排序获取热门商品
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 0)  // 上架状态（0=启用，1=禁用）
               .eq(Product::getDeleted, 0)  // 未删除
               .orderByDesc(Product::getSales)
               .last("LIMIT " + limit);
        return productMapper.selectList(wrapper);
    }

    @Override
    public List<Product> getNewProducts(int limit) {
        // 按创建时间排序获取新品
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 0)  // 上架状态（0=启用，1=禁用）
               .eq(Product::getDeleted, 0)  // 未删除
               .orderByDesc(Product::getCreateTime)
               .last("LIMIT " + limit);
        return productMapper.selectList(wrapper);
    }
}
