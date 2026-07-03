package com.example.system.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.result.Result;
import com.example.system.entity.Category;
import com.example.system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 分页查询分类列表
     */
    @GetMapping("/page")
    public Result<IPage<Category>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) Long parentId) {
        
        Page<Category> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        
        if (categoryName != null && !categoryName.isEmpty()) {
            wrapper.like(Category::getCategoryName, categoryName);
        }
        
        if (parentId != null) {
            wrapper.eq(Category::getParentId, parentId);
        }
        
        wrapper.eq(Category::getDeleted, 0);
        wrapper.orderByAsc(Category::getSortOrder);
        
        IPage<Category> result = categoryService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 获取所有分类（用于下拉选择）
     */
    @GetMapping("/all")
    public Result<List<Category>> listAll() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, 0);
        wrapper.eq(Category::getDeleted, 0);
        wrapper.orderByAsc(Category::getSortOrder);
        List<Category> list = categoryService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 根据ID查询分类
     */
    @GetMapping("/{categoryId}")
    public Result<Category> getById(@PathVariable Long categoryId) {
        Category category = categoryService.getById(categoryId);
        return Result.success(category);
    }

    /**
     * 新增分类
     */
    @PostMapping
    public Result<Category> save(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success("分类创建成功", category);
    }

    /**
     * 修改分类
     */
    @PutMapping("/{categoryId}")
    public Result<Category> update(@PathVariable Long categoryId, @RequestBody Category category) {
        category.setCategoryId(categoryId);
        categoryService.updateById(category);
        return Result.success("分类更新成功", category);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{categoryId}")
    public Result<String> delete(@PathVariable Long categoryId) {
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setDeleted(1);
        categoryService.updateById(category);
        return Result.success("分类删除成功", null);
    }

    /**
     * 启用/停用分类
     */
    @PutMapping("/{categoryId}/status")
    public Result<String> updateStatus(
            @PathVariable Long categoryId,
            @RequestParam Integer status) {
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setStatus(status);
        categoryService.updateById(category);
        return Result.success(status == 0 ? "分类已启用" : "分类已停用", null);
    }
}
