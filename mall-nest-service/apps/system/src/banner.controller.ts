import { Controller, Get, Post, Put, Delete, Body, Param, Query } from '@nestjs/common';
import { BannerService } from './banner.service';

@Controller('admin/banner')
export class BannerController {
  constructor(private bannerService: BannerService) {}

  /**
   * 获取轮播图列表（管理员）
   */
  @Get('list')
  async getBannerList() {
    return this.bannerService.getBannerList();
  }

  /**
   * 获取轮播图详情
   */
  @Get(':id')
  async getBannerDetail(@Param('id', ParseIntPipe) id: number) {
    return this.bannerService.getBannerDetail(id);
  }

  /**
   * 新增轮播图
   */
  @Post('add')
  async addBanner(@Body() bannerData: any) {
    return this.bannerService.addBanner(bannerData);
  }

  /**
   * 更新轮播图
   */
  @Put('update')
  async updateBanner(@Body() bannerData: any) {
    return this.bannerService.updateBanner(bannerData);
  }

  /**
   * 删除轮播图
   */
  @Delete(':id')
  async deleteBanner(@Param('id', ParseIntPipe) id: number) {
    return this.bannerService.deleteBanner(id);
  }

  /**
   * 启用/禁用轮播图
   */
  @Put('status/:id')
  async updateStatus(
    @Param('id', ParseIntPipe) id: number,
    @Query('isActive') isActive: boolean,
  ) {
    return this.bannerService.updateStatus(id, isActive);
  }
}

@Controller('home')
export class HomeController {
  constructor(private bannerService: BannerService) {}

  /**
   * 获取首页轮播图列表（前端）
   */
  @Get('banners')
  async getBanners() {
    return this.bannerService.getActiveBanners();
  }
}
