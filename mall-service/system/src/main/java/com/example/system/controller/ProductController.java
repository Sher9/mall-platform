package com.example.system.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.result.Result;
import com.example.system.dto.ProductDTO;
import com.example.system.entity.Product;
import com.example.system.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @GetMapping
    @SentinelResource(value = "productList", blockHandler = "blockHandler")
    public Result<IPage<Product>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) String productName) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        IPage<Product> result = productService.page(page, productName);
        return Result.success(result);
    }

    @GetMapping("/{productId}")
    public Result<Product> getById(@PathVariable Long productId) {
        log.info("查询产品详情, productId={}", productId);
        return Result.success(productService.getById(productId));
    }

    @PostMapping
    public Result<Product> save(@RequestBody ProductDTO productDTO) {
        // 将DTO转换为Entity
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        
        Product saved = productService.save(product, productDTO.getStockCount());
        return Result.success("产品创建成功", saved);
    }

    @PutMapping
    public Result<Product> update(@RequestBody ProductDTO productDTO) {
        // 将DTO转换为Entity
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        
        Product updated = productService.update(product);
        return Result.success("产品更新成功", updated);
    }

    @DeleteMapping("/{productId}")
    public Result<Void> delete(@PathVariable Long productId) {
        productService.delete(productId);
        return Result.success("产品删除成功", null);
    }

    @GetMapping("/all")
    public Result<List<Product>> list() {
        return Result.success(productService.list());
    }

    /**
     * 获取热门推荐商品（按销量排序取前4条）
     */
    @GetMapping("/hot")
    public Result<List<Product>> getHotProducts() {
        List<Product> hotProducts = productService.getHotProducts(4);
        return Result.success(hotProducts);
    }

    /**
     * 获取新品推荐商品（按创建时间排序取前3条）
     */
    @GetMapping("/new")
    public Result<List<Product>> getNewProducts() {
        List<Product> newProducts = productService.getNewProducts(3);
        return Result.success(newProducts);
    }

    public Result<IPage<Product>> blockHandler(Integer pageNum, Integer pageSize, String productName, Throwable e) {
        return Result.error(429, "请求过于频繁，请稍后重试");
    }
}
