import { Injectable } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';
import { SeataService } from '../../../libs/common/src/modules/seata/seata.service';
import { RedisService } from '../../../libs/common/src/modules/redis/redis.service';
import * as axios from 'axios';
import * as ExcelJS from 'exceljs';
import { Response } from 'express';

@Injectable()
export class OrderService {
  private prisma: PrismaClient;
  private readonly CACHE_PREFIX = 'order:';

  constructor(
    private seataService: SeataService,
    private redisService: RedisService,
  ) {
    this.prisma = new PrismaClient();
  }

  /**
   * 创建订单 - 使用 Seata 分布式事务
   */
  async createOrder(customerId: number, items: any[], address: string) {
    return await this.seataService.executeInTransaction(async (txId) => {
      console.log(`开始创建订单，事务ID: ${txId}`);

      // 计算总金额
      let totalAmount = 0;
      for (const item of items) {
        const product = await this.prisma.product.findUnique({
          where: { id: item.productId },
        });
        if (product) {
          totalAmount += product.price * item.quantity;
        }
      }

      // 1. 创建订单
      const order = await this.prisma.order.create({
        data: {
          orderNo: `ORD${Date.now()}${Math.random().toString(36).substring(2, 6).toUpperCase()}`,
          customerId,
          totalAmount,
          status: 0, // 待支付
          address,
          items: {
            create: items.map(item => ({
              productId: item.productId,
              productName: item.productName,
              productImage: item.productImage,
              price: item.price,
              quantity: item.quantity,
            })),
          },
        },
        include: {
          items: true,
        },
      });

      // 2. 调用库存服务扣减库存
      await this.seataService.registerBranchTransaction(txId, 'stock-deduct');
      try {
        const stockResponse = await axios.put('http://localhost:9007/api/stock/batchDeduct', {
          items: items.map(item => ({
            productId: item.productId,
            quantity: item.quantity,
          })),
        });
        if (stockResponse.data.code !== 200) {
          throw new Error('库存扣减失败');
        }
      } catch (error) {
        throw new Error(`库存服务调用失败: ${error.message}`);
      }

      // 3. 调用支付服务创建支付订单
      await this.seataService.registerBranchTransaction(txId, 'pay-create');
      try {
        const payResponse = await axios.post('http://localhost:9005/api/pay/create', {
          orderNo: order.orderNo,
          customerId,
          amount: totalAmount,
          payType: 1, // 默认微信支付
        });
        if (payResponse.data.code !== 200) {
          throw new Error('支付订单创建失败');
        }
      } catch (error) {
        throw new Error(`支付服务调用失败: ${error.message}`);
      }

      console.log(`订单创建成功，事务ID: ${txId}`);
      return { code: 200, message: '创建成功', data: order };
    });
  }

  /**
   * 获取订单列表
   */
  async getOrderList(customerId: number, status?: number, pageNum: number = 1, pageSize: number = 10) {
    try {
      const skip = (pageNum - 1) * pageSize;
      const where: any = { customerId };
      if (status !== undefined) {
        where.status = status;
      }

      const [records, total] = await Promise.all([
        this.prisma.order.findMany({
          where,
          skip,
          take: pageSize,
          orderBy: { createTime: 'desc' },
          include: {
            items: true,
          },
        }),
        this.prisma.order.count({ where }),
      ]);

      return {
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
    } catch (error) {
      return { code: 500, message: `获取失败: ${error.message}`, data: null };
    }
  }

  /**
   * 获取订单详情
   */
  async getOrderDetail(orderNo: string) {
    try {
      // 尝试从缓存获取
      const cacheKey = `${this.CACHE_PREFIX}detail:${orderNo}`;
      const cachedData = await this.redisService.get(cacheKey);
      if (cachedData) {
        return { code: 200, message: '获取成功', data: cachedData };
      }

      const order = await this.prisma.order.findUnique({
        where: { orderNo },
        include: {
          items: true,
        },
      });

      if (!order) {
        return { code: 404, message: '订单不存在', data: null };
      }

      // 保存到缓存
      await this.redisService.set(cacheKey, order, 300);

      return { code: 200, message: '获取成功', data: order };
    } catch (error) {
      return { code: 500, message: `获取失败: ${error.message}`, data: null };
    }
  }

  /**
   * 取消订单 - 使用 Seata 分布式事务
   */
  async cancelOrder(orderNo: string) {
    return await this.seataService.executeInTransaction(async (txId) => {
      console.log(`开始取消订单，事务ID: ${txId}`);

      // 1. 获取订单信息
      const order = await this.prisma.order.findUnique({
        where: { orderNo },
        include: {
          items: true,
        },
      });

      if (!order) {
        throw new Error('订单不存在');
      }

      if (order.status === 4) {
        throw new Error('订单已取消');
      }

      // 2. 更新订单状态为已取消
      await this.prisma.order.update({
        where: { orderNo },
        data: { status: 4 }, // 已取消
      });

      // 3. 调用库存服务恢复库存
      await this.seataService.registerBranchTransaction(txId, 'stock-restore');
      try {
        for (const item of order.items) {
          await axios.put('http://localhost:9007/api/stock/add', {
            productId: item.productId,
            quantity: item.quantity,
          });
        }
      } catch (error) {
        throw new Error(`库存服务调用失败: ${error.message}`);
      }

      // 4. 调用支付服务关闭支付订单
      await this.seataService.registerBranchTransaction(txId, 'pay-close');
      try {
        await axios.post('http://localhost:9005/api/pay/close', {
          payNo: order.orderNo,
        });
      } catch (error) {
        throw new Error(`支付服务调用失败: ${error.message}`);
      }

      // 5. 清除缓存
      await this.redisService.del(`${this.CACHE_PREFIX}detail:${orderNo}`);

      console.log(`订单取消成功，事务ID: ${txId}`);
      return { code: 200, message: '取消成功', data: null };
    });
  }

  /**
   * 支付订单
   */
  async payOrder(orderNo: string, payType: number) {
    try {
      const order = await this.prisma.order.findUnique({
        where: { orderNo },
      });

      if (!order) {
        return { code: 404, message: '订单不存在', data: null };
      }

      if (order.status !== 0) {
        return { code: 400, message: '订单状态不允许支付', data: null };
      }

      // 调用支付服务
      const payResponse = await axios.post('http://localhost:9005/api/pay/create', {
        orderNo,
        customerId: order.customerId,
        amount: order.totalAmount,
        payType,
      });

      return { code: 200, message: '支付订单创建成功', data: payResponse.data.data };
    } catch (error) {
      return { code: 500, message: `支付失败: ${error.message}`, data: null };
    }
  }

  /**
   * 发货
   */
  async shipOrder(orderNo: string, trackingNo: string) {
    try {
      const order = await this.prisma.order.findUnique({
        where: { orderNo },
      });

      if (!order) {
        return { code: 404, message: '订单不存在', data: null };
      }

      if (order.status !== 1) {
        return { code: 400, message: '订单状态不允许发货', data: null };
      }

      const updatedOrder = await this.prisma.order.update({
        where: { orderNo },
        data: {
          status: 2, // 已发货
          shipTime: new Date(),
        },
      });

      // 清除缓存
      await this.redisService.del(`${this.CACHE_PREFIX}detail:${orderNo}`);

      return { code: 200, message: '发货成功', data: updatedOrder };
    } catch (error) {
      return { code: 500, message: `发货失败: ${error.message}`, data: null };
    }
  }

  /**
   * 确认收货
   */
  async confirmOrder(orderNo: string) {
    try {
      const order = await this.prisma.order.findUnique({
        where: { orderNo },
      });

      if (!order) {
        return { code: 404, message: '订单不存在', data: null };
      }

      if (order.status !== 2) {
        return { code: 400, message: '订单状态不允许确认收货', data: null };
      }

      const updatedOrder = await this.prisma.order.update({
        where: { orderNo },
        data: {
          status: 3, // 已收货
          confirmTime: new Date(),
        },
      });

      // 清除缓存
      await this.redisService.del(`${this.CACHE_PREFIX}detail:${orderNo}`);

      return { code: 200, message: '确认收货成功', data: updatedOrder };
    } catch (error) {
      return { code: 500, message: `确认收货失败: ${error.message}`, data: null };
    }
  }

  /**
   * 导出订单数据
   * @param ids 订单ID数组，为空则导出所有
   * @param res Express Response 对象
   */
  async exportOrder(ids: number[] | null, res: Response) {
    try {
      // 1. 查询数据
      const where: any = {};
      if (ids && ids.length > 0) {
        where.id = { in: ids };
      }

      const orders = await this.prisma.order.findMany({
        where,
        orderBy: { createTime: 'desc' },
        include: {
          items: true,
        },
      });

      // 2. 创建 Excel 工作簿
      const workbook = new ExcelJS.Workbook();
      const worksheet = workbook.addWorksheet('订单数据');

      // 3. 创建表头
      worksheet.columns = [
        { header: 'ID', key: 'id', width: 10 },
        { header: '订单号', key: 'orderNo', width: 20 },
        { header: '客户ID', key: 'customerId', width: 10 },
        { header: '总金额', key: 'totalAmount', width: 12 },
        { header: '状态', key: 'status', width: 10 },
        { header: '收货地址', key: 'address', width: 30 },
        { header: '创建时间', key: 'createTime', width: 20 },
        { header: '支付时间', key: 'payTime', width: 20 },
        { header: '发货时间', key: 'shipTime', width: 20 },
      ];

      // 4. 填充数据
      orders.forEach(order => {
        worksheet.addRow({
          id: order.id,
          orderNo: order.orderNo,
          customerId: order.customerId,
          totalAmount: order.totalAmount,
          status: order.status,
          address: order.address,
          createTime: order.createTime ? order.createTime.toISOString() : '',
          payTime: order.payTime ? order.payTime.toISOString() : '',
          shipTime: order.shipTime ? order.shipTime.toISOString() : '',
        });
      });

      // 5. 设置响应头
      res.setHeader('Content-Type', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
      res.setHeader('Content-Disposition', `attachment; filename=${encodeURIComponent('订单数据.xlsx')}`);

      // 6. 写入响应
      await workbook.xlsx.write(res);
      res.end();
    } catch (error) {
      console.error('导出订单数据失败:', error);
      throw new Error(`导出失败: ${error.message}`);
    }
  }
}
