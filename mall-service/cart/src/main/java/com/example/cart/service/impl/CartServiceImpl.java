package com.example.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.cart.entity.CartItem;
import com.example.cart.mapper.CartMapper;
import com.example.cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车服务实现
 */
@Slf4j
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, CartItem> implements CartService {

    @Override
    public CartItem addToCart(Long customerId, Long productId, Long skuId, Integer quantity) {
        // 检查是否已存在
        CartItem existItem = getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCustomerId, customerId)
                .eq(CartItem::getProductId, productId)
                .eq(CartItem::getSkuId, skuId));

        if (existItem != null) {
            // 已存在，增加数量
            existItem.setQuantity(existItem.getQuantity() + quantity);
            updateById(existItem);
            return existItem;
        } else {
            // 新增
            CartItem newItem = new CartItem();
            newItem.setCustomerId(customerId);
            newItem.setProductId(productId);
            newItem.setSkuId(skuId);
            newItem.setQuantity(quantity);
            newItem.setSelected(true);
            save(newItem);
            return newItem;
        }
    }

    @Override
    public List<CartItem> getCartList(Long customerId) {
        return list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCustomerId, customerId)
                .orderByDesc(CartItem::getCreatedAt));
    }

    @Override
    public boolean updateQuantity(Long customerId, Long cartItemId, Integer quantity) {
        CartItem item = getById(cartItemId);
        if (item == null || !item.getCustomerId().equals(customerId)) {
            return false;
        }
        item.setQuantity(quantity);
        return updateById(item);
    }

    @Override
    public boolean removeFromCart(Long customerId, Long cartItemId) {
        CartItem item = getById(cartItemId);
        if (item == null || !item.getCustomerId().equals(customerId)) {
            return false;
        }
        return removeById(cartItemId);
    }

    @Override
    public boolean removeBatch(Long customerId, List<Long> cartItemIds) {
        return remove(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCustomerId, customerId)
                .in(CartItem::getId, cartItemIds));
    }

    @Override
    public boolean updateSelected(Long customerId, Long cartItemId, Boolean selected) {
        CartItem item = getById(cartItemId);
        if (item == null || !item.getCustomerId().equals(customerId)) {
            return false;
        }
        item.setSelected(selected);
        return updateById(item);
    }

    @Override
    public boolean updateAllSelected(Long customerId, Boolean selected) {
        List<CartItem> items = getCartList(customerId);
        for (CartItem item : items) {
            item.setSelected(selected);
        }
        return updateBatchById(items);
    }

    @Override
    public Integer getCartCount(Long customerId) {
        return count(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCustomerId, customerId));
    }
}
