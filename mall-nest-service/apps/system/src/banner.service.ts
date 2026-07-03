import { Injectable } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';

@Injectable()
export class BannerService {
  private prisma: PrismaClient;

  constructor() {
    this.prisma = new PrismaClient();
  }

  /**
   * 获取轮播图列表（管理员）
   */
  async getBannerList() {
    const banners = await this.prisma.banner.findMany({
      orderBy: { sort: 'asc' },
    });
    return {
      code: 200,
      message: '获取成功',
      data: banners,
    };
  }

  /**
   * 获取轮播图详情
   */
  async getBannerDetail(id: number) {
    const banner = await this.prisma.banner.findUnique({
      where: { id },
    });
    return {
      code: 200,
      message: '获取成功',
      data: banner,
    };
  }

  /**
   * 新增轮播图
   */
  async addBanner(bannerData: any) {
    const banner = await this.prisma.banner.create({
      data: {
        title: bannerData.title,
        imageUrl: bannerData.imageUrl,
        linkUrl: bannerData.linkUrl,
        sort: bannerData.sort || 0,
        isActive: bannerData.isActive !== undefined ? bannerData.isActive : true,
      },
    });
    return {
      code: 200,
      message: '新增成功',
      data: banner,
    };
  }

  /**
   * 更新轮播图
   */
  async updateBanner(bannerData: any) {
    const banner = await this.prisma.banner.update({
      where: { id: bannerData.id },
      data: {
        title: bannerData.title,
        imageUrl: bannerData.imageUrl,
        linkUrl: bannerData.linkUrl,
        sort: bannerData.sort,
        isActive: bannerData.isActive,
      },
    });
    return {
      code: 200,
      message: '更新成功',
      data: banner,
    };
  }

  /**
   * 删除轮播图
   */
  async deleteBanner(id: number) {
    await this.prisma.banner.delete({
      where: { id },
    });
    return {
      code: 200,
      message: '删除成功',
      data: null,
    };
  }

  /**
   * 启用/禁用轮播图
   */
  async updateStatus(id: number, isActive: boolean) {
    await this.prisma.banner.update({
      where: { id },
      data: { isActive },
    });
    return {
      code: 200,
      message: '更新成功',
      data: null,
    };
  }

  /**
   * 获取启用的轮播图列表（前端）
   */
  async getActiveBanners() {
    const banners = await this.prisma.banner.findMany({
      where: { isActive: true },
      orderBy: { sort: 'asc' },
    });
    return {
      code: 200,
      message: '获取成功',
      data: banners,
    };
  }
}
