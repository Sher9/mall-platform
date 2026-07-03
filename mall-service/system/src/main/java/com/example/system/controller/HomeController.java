package com.example.system.controller;

import com.example.common.result.Result;
import com.example.system.entity.Banner;
import com.example.system.entity.Category;
import com.example.system.entity.Product;
import com.example.system.service.BannerService;
import com.example.system.service.CategoryService;
import com.example.system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页控制器（小程序/App）
 */
@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final BannerService bannerService;
    private final CategoryService categoryService;
    private final ProductService productService;

    /**
     * 获取首页数据
     * 包含：轮播图、分类、热门商品、新品推荐
     */
    @GetMapping("/index")
    public Result<Map<String, Object>> getHomeData() {
        Map<String, Object> data = new HashMap<>();
        
        // 轮播图
        List<Banner> banners = bannerService.getActiveBanners();
        data.put("banners", banners);
        
        // 商品分类（一级分类）
        List<Category> categories = categoryService.getRootCategories();
        data.put("categories", categories);
        
        // 热门推荐商品
        List<Product> hotProducts = productService.getHotProducts(10);
        data.put("hotProducts", hotProducts);
        
        // 新品推荐
        List<Product> newProducts = productService.getNewProducts(10);
        data.put("newProducts", newProducts);
        
        return Result.success(data);
    }

    /**
     * 获取轮播图列表
     */
    @GetMapping("/banners")
    public Result<List<Banner>> getBanners() {
        List<Banner> banners = bannerService.getActiveBanners();
        return Result.success(banners);
    }

    /**
     * 获取商品分类
     */
    @GetMapping("/categories")
    public Result<List<Category>> getCategories(@RequestParam(required = false) Long parentId) {
        List<Category> categories;
        if (parentId != null) {
            categories = categoryService.getCategoriesByParentId(parentId);
        } else {
            categories = categoryService.getRootCategories();
        }
        return Result.success(categories);
    }

    /**
     * 获取热门推荐商品
     */
    @GetMapping("/hot")
    public Result<List<Product>> getHotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        List<Product> products = productService.getHotProducts(limit);
        return Result.success(products);
    }

    /**
     * 获取新品推荐
     */
    @GetMapping("/new")
    public Result<List<Product>> getNewProducts(@RequestParam(defaultValue = "10") Integer limit) {
        List<Product> products = productService.getNewProducts(limit);
        return Result.success(products);
    }
}
