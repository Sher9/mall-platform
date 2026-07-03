import { Injectable } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';
import { RedisService } from '../../../libs/common/src/modules/redis/redis.service';

@Injectable()
export class CartService {
  private prisma: PrismaClient;
  private readonly CACHE_PREFIX = 'cart:';

  constructor(private redisService: RedisService) {
    this.prisma = new PrismaClient();
  }

  /**
   * 添加商品到购物车
   */
  async addToCart(customerId: number, productId: number, quantity: number, skuId?: number) {
    try {
      // 检查商品是否存在
      const product = await this.prisma.product.findUnique({
        where: { id: productId },
      });

      if (!product) {
        return { code: 404, message: '商品不存在', data: null };
      }

      // 检查购物车是否已有该商品
      const existingItem = await this.prisma.cartItem.findFirst({
        where: {
          customerId,
          productId,
          skuId: skuId || null,
        },
      });

      if (existingItem) {
        // 已有则更新数量
        const updatedItem = await this.prisma.cartItem.update({
          where: { id: existingItem.id },
          data: { quantity: existingItem.quantity + quantity },
        });

        // 清除缓存
        await this.clearCartCache(customerId);

        return { code: 200, message: '添加成功', data: updatedItem };
      } else {
        // 没有则新增
        const cartItem = await this.prisma.cartItem.create({
          data: {
            customerId,
            productId,
            skuId,
            productName: product.productName,
            productImage: product.image,
            price: product.price,
            quantity,
            selected: true,
          },
        });

        // 清除缓存
        await this.clearCartCache(customerId);

        return { code: 200, message: '添加成功', data: cartItem };
      }
    } catch (error) {
      return { code: 500, message: `添加失败: ${error.message}`, data: null };
    }
  }

  /**
   * 获取购物车列表
   */
  async getCartList(customerId: number) {
    try {
      // 尝试从缓存获取
      const cacheKey = `${this.CACHE_PREFIX}list:${customerId}`;
      const cachedData = await this.redisService.get(cacheKey);
      if (cachedData) {
        return { code: 200, message: '获取成功', data: cachedData };
      }

      const cartItems = await this.prisma.cartItem.findMany({
        where: { customerId },
        orderBy: { createdAt: 'desc' },
      });

      // 保存到缓存
      await this.redisService.set(cacheKey, cartItems, 300);

      return { code: 200, message: '获取成功', data: cartItems };
    } catch (error) {
      return { code: 500, message: `获取失败: ${error.message}`, data: null };
    }
  }

  /**
   * 更新商品数量
   */
  async updateQuantity(cartItemId: number, quantity: number) {
    try {
      const cartItem = await this.prisma.cartItem.findUnique({
        where: { id: cartItemId },
      });

      if (!cartItem) {
        return { code: 404, message: '购物车商品不存在', data: null };
      }

      const updatedItem = await this.prisma.cartItem.update({
        where: { id: cartItemId },
        data: { quantity },
      });

      // 清除缓存
      await this.clearCartCache(cartItem.customerId);

      return { code: 200, message: '更新成功', data: updatedItem };
    } catch (error) {
      return { code: 500, message: `更新失败: ${error.message}`, data: null };
    }
  }

  /**
   * 删除购物车商品
   */
  async deleteCartItem(customerId: number, cartItemId: number) {
    try {
      const cartItem = await this.prisma.cartItem.findUnique({
        where: { id: cartItemId },
      });

      if (!cartItem || cartItem.customerId !== customerId) {
        return { code: 404, message: '购物车商品不存在', data: null };
      }

      await this.prisma.cartItem.delete({
        where: { id: cartItemId },
      });

      // 清除缓存
      await this.clearCartCache(customerId);

      return { code: 200, message: '删除成功', data: null };
    } catch (error) {
      return { code: 500, message: `删除失败: ${error.message}`, data: null };
    }
  }

  /**
   * 批量删除购物车商品
   */
  async batchDelete(customerId: number, cartItemIds: number[]) {
    try {
      await this.prisma.cartItem.deleteMany({
        where: {
          customerId,
          id: { in: cartItemIds },
        },
      });

      // 清除缓存
      await this.clearCartCache(customerId);

      return { code: 200, message: '批量删除成功', data: null };
    } catch (error) {
      return { code: 500, message: `批量删除失败: ${error.message}`, data: null };
    }
  }

  /**
   * 更新选中状态
   */
  async updateSelected(customerId: number, cartItemId: number, selected: boolean) {
    try {
      const cartItem = await this.prisma.cartItem.findUnique({
        where: { id: cartItemId },
      });

      if (!cartItem || cartItem.customerId !== customerId) {
        return { code: 404, message: '购物车商品不存在', data: null };
      }

      const updatedItem = await this.prisma.cartItem.update({
        where: { id: cartItemId },
        data: { selected },
      });

      // 清除缓存
      await this.clearCartCache(customerId);

      return { code: 200, message: '更新成功', data: updatedItem };
    } catch (error) {
      return { code: 500, message: `更新失败: ${error.message}`, data: null };
    }
  }

  /**
   * 全选/取消全选
   */
  async selectAll(customerId: number, selected: boolean) {
    try {
      await this.prisma.cartItem.updateMany({
        where: { customerId },
        data: { selected },
      });

      // 清除缓存
      await this.clearCartCache(customerId);

      return { code: 200, message: '操作成功', data: null };
    } catch (error) {
      return { code: 500, message: `操作失败: ${error.message}`, data: null };
    }
  }

  /**
   * 获取购物车数量
   */
  async getCartCount(customerId: number) {
    try {
      const count = await this.prisma.cartItem.count({
        where: { customerId },
      });

      return { code: 200, message: '获取成功', data: { count } };
    } catch (error) {
      return { code: 500, message: `获取失败: ${error.message}`, data: null };
    }
  }

  /**
   * 清除购物车缓存
   */
  private async clearCartCache(customerId: number) {
    await this.redisService.del(`${this.CACHE_PREFIX}list:${customerId}`);
    await this.redisService.del(`${this.CACHE_PREFIX}count:${customerId}`);
  }
}
