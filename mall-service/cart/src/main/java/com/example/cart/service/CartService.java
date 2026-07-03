package com.example.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.cart.entity.CartItem;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService extends IService<CartItem> {

    /**
     * 添加商品到购物车
     */
    CartItem addToCart(Long customerId, Long productId, Long skuId, Integer quantity);

    /**
     * 获取用户购物车列表
     */
    List<CartItem> getCartList(Long customerId);

    /**
     * 更新购物车商品数量
     */
    boolean updateQuantity(Long customerId, Long cartItemId, Integer quantity);

    /**
     * 删除购物车商品
     */
    boolean removeFromCart(Long customerId, Long cartItemId);

    /**
     * 批量删除购物车商品
     */
    boolean removeBatch(Long customerId, List<Long> cartItemIds);

    /**
     * 更新商品选中状态
     */
    boolean updateSelected(Long customerId, Long cartItemId, Boolean selected);

    /**
     * 全选/取消全选
     */
    boolean updateAllSelected(Long customerId, Boolean selected);

    /**
     * 获取购物车商品数量
     */
    Integer getCartCount(Long customerId);
}
