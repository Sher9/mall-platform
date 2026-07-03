package com.example.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.system.entity.Product;

import java.util.List;

public interface ProductService {
    IPage<Product> page(Page<Product> page, String productName);
    Product getById(Long productId);
    /**
     * 新增商品
     * @param product 商品信息
     * @param stockCount 库存数量
     */
    Product save(Product product, Integer stockCount);
    Product update(Product product);
    void delete(Long productId);
    List<Product> list();
    
    /**
     * 获取热门推荐商品
     */
    List<Product> getHotProducts(int limit);
    
    /**
     * 获取新品推荐商品
     */
    List<Product> getNewProducts(int limit);
}
