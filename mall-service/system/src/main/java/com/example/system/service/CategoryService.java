package com.example.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.system.entity.Category;

import java.util.List;

public interface CategoryService {
    /**
     * 分页查询分类列表
     */
    IPage<Category> page(Page<Category> page, com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category> wrapper);

    /**
     * 根据条件查询分类列表
     */
    List<Category> list(com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category> wrapper);

    /**
     * 根据ID查询分类
     */
    Category getById(Long categoryId);

    /**
     * 保存分类
     */
    void save(Category category);

    /**
     * 根据ID更新分类
     */
    void updateById(Category category);

    /**
     * 获取一级分类（parentId为0或null）
     */
    List<Category> getRootCategories();

    /**
     * 根据父分类ID获取子分类
     */
    List<Category> getCategoriesByParentId(Long parentId);

    /**
     * 获取所有活跃分类
     */
    List<Category> getActiveCategories();
}
