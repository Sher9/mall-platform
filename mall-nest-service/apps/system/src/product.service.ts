import { Injectable } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';
import { RedisService } from '../../../libs/common/src/modules/redis/redis.service';

@Injectable()
export class ProductService {
  private prisma: PrismaClient;
  private readonly CACHE_PREFIX = 'product:';
  private readonly CACHE_TTL = 300; // 5 分钟

  constructor(private redisService: RedisService) {
    this.prisma = new PrismaClient();
  }

  /**
   * 获取商品列表（分页）- 使用 Redis 缓存
   */
  async getProductList(pageNum: number, pageSize: number, productName?: string) {
    // 生成缓存 key
    const cacheKey = `${this.CACHE_PREFIX}list:${pageNum}:${pageSize}:${productName || 'all'}`;
    
    // 尝试从缓存获取
    const cachedData = await this.redisService.get(cacheKey);
    if (cachedData) {
      return cachedData;
    }

    // 从数据库查询
    const skip = (pageNum - 1) * pageSize;
    const where: any = {};
    if (productName) {
      where.productName = { contains: productName };
    }

    const [records, total] = await Promise.all([
      this.prisma.product.findMany({
        where,
        skip,
        take: pageSize,
        orderBy: { createTime: 'desc' },
      }),
      this.prisma.product.count({ where }),
    ]);

    const result = {
      code: 200,
      message: '获取成功',
      data: {
        records,
        total,
        size: pageSize,
        current: pageNum,
        pages: Math.ceil(total / pageSize),
      },
    };

    // 保存到缓存
    await this.redisService.set(cacheKey, result, this.CACHE_TTL);

    return result;
  }

  /**
   * 获取商品详情 - 使用 Redis 缓存
   */
  async getProductDetail(id: number) {
    // 生成缓存 key
    const cacheKey = `${this.CACHE_PREFIX}detail:${id}`;
    
    // 尝试从缓存获取
    const cachedData = await this.redisService.get(cacheKey);
    if (cachedData) {
      return cachedData;
    }

    // 从数据库查询
    const product = await this.prisma.product.findUnique({
      where: { id },
    });

    const result = {
      code: 200,
      message: '获取成功',
      data: product,
    };

    // 保存到缓存
    await this.redisService.set(cacheKey, result, this.CACHE_TTL);

    return result;
  }

  /**
   * 新增商品 - 清除相关缓存
   */
  async addProduct(productData: any) {
    const product = await this.prisma.product.create({
      data: {
        productNo: `PROD${Date.now()}${Math.random().toString(36).substring(2, 6).toUpperCase()}`,
        productName: productData.name || productData.productName,
        categoryId: productData.categoryId || 0,
        price: productData.price || 0,
        originalPrice: productData.originalPrice,
        stock: productData.stock || 0,
        image: productData.image,
        images: productData.images,
        description: productData.description,
        detail: productData.detail,
        status: productData.status || 0,
      },
    });

    // 清除商品列表缓存
    await this.clearProductCache();

    return {
      code: 200,
      message: '新增成功',
      data: product,
    };
  }

  /**
   * 更新商品 - 清除相关缓存
   */
  async updateProduct(productData: any) {
    const product = await this.prisma.product.update({
      where: { id: productData.id },
      data: {
        productName: productData.name || productData.productName,
        categoryId: productData.categoryId,
        price: productData.price,
        originalPrice: productData.originalPrice,
        stock: productData.stock,
        image: productData.image,
        images: productData.images,
        description: productData.description,
        detail: productData.detail,
        status: productData.status,
      },
    });

    // 清除商品详情缓存和列表缓存
    await this.redisService.del(`${this.CACHE_PREFIX}detail:${productData.id}`);
    await this.clearProductCache();

    return {
      code: 200,
      message: '更新成功',
      data: product,
    };
  }

  /**
   * 删除商品 - 清除相关缓存
   */
  async deleteProduct(id: number) {
    await this.prisma.product.delete({
      where: { id },
    });

    // 清除商品详情缓存和列表缓存
    await this.redisService.del(`${this.CACHE_PREFIX}detail:${id}`);
    await this.clearProductCache();

    return {
      code: 200,
      message: '删除成功',
      data: null,
    };
  }

  /**
   * 获取热门推荐商品 - 使用 Redis 缓存
   */
  async getHotProducts(limit: number) {
    // 生成缓存 key
    const cacheKey = `${this.CACHE_PREFIX}hot:${limit}`;
    
    // 尝试从缓存获取
    const cachedData = await this.redisService.get(cacheKey);
    if (cachedData) {
      return cachedData;
    }

    // 从数据库查询
    const products = await this.prisma.product.findMany({
      where: { status: 1 },
      orderBy: { sales: 'desc' },
      take: limit,
    });

    // 保存到缓存
    await this.redisService.set(cacheKey, products, this.CACHE_TTL);

    return products;
  }

  /**
   * 获取新品推荐商品 - 使用 Redis 缓存
   */
  async getNewProducts(limit: number) {
    // 生成缓存 key
    const cacheKey = `${this.CACHE_PREFIX}new:${limit}`;
    
    // 尝试从缓存获取
    const cachedData = await this.redisService.get(cacheKey);
    if (cachedData) {
      return cachedData;
    }

    // 从数据库查询
    const products = await this.prisma.product.findMany({
      where: { status: 1 },
      orderBy: { createTime: 'desc' },
      take: limit,
    });

    // 保存到缓存
    await this.redisService.set(cacheKey, products, this.CACHE_TTL);

    return products;
  }

  /**
   * 清除商品列表缓存
   */
  private async clearProductCache() {
    await this.redisService.delPattern(`${this.CACHE_PREFIX}list:*`);
    await this.redisService.delPattern(`${this.CACHE_PREFIX}hot:*`);
    await this.redisService.delPattern(`${this.CACHE_PREFIX}new:*`);
  }
}

