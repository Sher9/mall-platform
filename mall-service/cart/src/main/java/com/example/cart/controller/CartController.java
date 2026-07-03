package com.example.cart.controller;

import com.example.common.domain.R;
import com.example.cart.entity.CartItem;
import com.example.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public R<CartItem> addToCart(
            @RequestParam Long customerId,
            @RequestParam Long productId,
            @RequestParam(required = false) Long skuId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        CartItem item = cartService.addToCart(customerId, productId, skuId, quantity);
        return R.ok(item);
    }

    /**
     * 获取购物车列表
     */
    @GetMapping("/list")
    public R<List<CartItem>> getCartList(@RequestParam Long customerId) {
        List<CartItem> list = cartService.getCartList(customerId);
        return R.ok(list);
    }

    /**
     * 更新商品数量
     */
    @PutMapping("/quantity")
    public R<Void> updateQuantity(
            @RequestParam Long customerId,
            @RequestParam Long cartItemId,
            @RequestParam Integer quantity) {
        boolean success = cartService.updateQuantity(customerId, cartItemId, quantity);
        return success ? R.ok() : R.fail("更新失败");
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/{cartItemId}")
    public R<Void> removeFromCart(
            @RequestParam Long customerId,
            @PathVariable Long cartItemId) {
        boolean success = cartService.removeFromCart(customerId, cartItemId);
        return success ? R.ok() : R.fail("删除失败");
    }

    /**
     * 批量删除购物车商品
     */
    @DeleteMapping("/batch")
    public R<Void> removeBatch(
            @RequestParam Long customerId,
            @RequestBody List<Long> cartItemIds) {
        boolean success = cartService.removeBatch(customerId, cartItemIds);
        return success ? R.ok() : R.fail("删除失败");
    }

    /**
     * 更新商品选中状态
     */
    @PutMapping("/selected")
    public R<Void> updateSelected(
            @RequestParam Long customerId,
            @RequestParam Long cartItemId,
            @RequestParam Boolean selected) {
        boolean success = cartService.updateSelected(customerId, cartItemId, selected);
        return success ? R.ok() : R.fail("更新失败");
    }

    /**
     * 全选/取消全选
     */
    @PutMapping("/selectAll")
    public R<Void> updateAllSelected(
            @RequestParam Long customerId,
            @RequestParam Boolean selected) {
        boolean success = cartService.updateAllSelected(customerId, selected);
        return success ? R.ok() : R.fail("更新失败");
    }

    /**
     * 获取购物车商品数量
     */
    @GetMapping("/count")
    public R<Integer> getCartCount(@RequestParam Long customerId) {
        Integer count = cartService.getCartCount(customerId);
        return R.ok(count);
    }
}
