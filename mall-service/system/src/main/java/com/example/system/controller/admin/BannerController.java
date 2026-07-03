package com.example.system.controller.admin;

import com.example.common.result.Result;
import com.example.system.entity.Banner;
import com.example.system.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图管理控制器（管理员）
 */
@RestController
@RequestMapping("/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    /**
     * 获取轮播图列表（分页）
     */
    @GetMapping("/list")
    public Result<List<Banner>> getBannerList() {
        List<Banner> list = bannerService.list();
        return Result.success(list);
    }

    /**
     * 获取轮播图详情
     */
    @GetMapping("/{id}")
    public Result<Banner> getBanner(@PathVariable Long id) {
        Banner banner = bannerService.getById(id);
        return Result.success(banner);
    }

    /**
     * 新增轮播图
     */
    @PostMapping("/add")
    public Result<Banner> addBanner(@RequestBody Banner banner) {
        bannerService.save(banner);
        return Result.success(banner);
    }

    /**
     * 更新轮播图
     */
    @PutMapping("/update")
    public Result<Void> updateBanner(@RequestBody Banner banner) {
        boolean success = bannerService.updateById(banner);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除轮播图
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteBanner(@PathVariable Long id) {
        boolean success = bannerService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 启用/禁用轮播图
     */
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive) {
        Banner banner = new Banner();
        banner.setId(id);
        banner.setIsActive(isActive);
        boolean success = bannerService.updateById(banner);
        return success ? Result.success() : Result.error("更新失败");
    }
}
