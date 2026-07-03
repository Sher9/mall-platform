import { Injectable } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';
import * as ExcelJS from 'exceljs';
import { Response } from 'express';

@Injectable()
export class StockService {
  private prisma: PrismaClient;

  constructor() {
    this.prisma = new PrismaClient();
  }

  /**
   * 查询库存
   */
  async getStock(productId: number) {
    try {
      let stock = await this.prisma.stock.findUnique({
        where: { productId },
      });

      // 如果库存记录不存在，则创建
      if (!stock) {
        stock = await this.prisma.stock.create({
          data: {
            productId,
            quantity: 0,
            locked: 0,
          },
        });
      }

      return {
        code: 200,
        message: '查询成功',
        data: {
          productId: stock.productId,
          quantity: stock.quantity,
          locked: stock.locked,
          available: stock.quantity - stock.locked,
        },
      };
    } catch (error) {
      return { code: 500, message: `查询失败: ${error.message}`, data: null };
    }
  }

  /**
   * 扣减库存
   */
  async deductStock(productId: number, quantity: number) {
    try {
      let stock = await this.prisma.stock.findUnique({
        where: { productId },
      });

      // 如果库存记录不存在，则创建
      if (!stock) {
        stock = await this.prisma.stock.create({
          data: {
            productId,
            quantity: 0,
            locked: 0,
          },
        });
      }

      // 检查库存是否充足
      const available = stock.quantity - stock.locked;
      if (available < quantity) {
        return { code: 400, message: '库存不足', data: null };
      }

      // 扣减库存
      const updatedStock = await this.prisma.stock.update({
        where: { productId },
        data: { quantity: stock.quantity - quantity },
      });

      return {
        code: 200,
        message: '扣减成功',
        data: {
          productId: updatedStock.productId,
          quantity: updatedStock.quantity,
          locked: updatedStock.locked,
          available: updatedStock.quantity - updatedStock.locked,
        },
      };
    } catch (error) {
      return { code: 500, message: `扣减失败: ${error.message}`, data: null };
    }
  }

  /**
   * 批量扣减库存
   */
  async batchDeduct(items: { productId: number; quantity: number }[]) {
    try {
      const results = [];

      for (const item of items) {
        const result = await this.deductStock(item.productId, item.quantity);
        if (result.code !== 200) {
          throw new Error(`商品${item.productId}库存不足`);
        }
        results.push(result.data);
      }

      return { code: 200, message: '批量扣减成功', data: results };
    } catch (error) {
      return { code: 500, message: `批量扣减失败: ${error.message}`, data: null };
    }
  }

  /**
   * 增加库存
   */
  async addStock(productId: number, quantity: number) {
    try {
      let stock = await this.prisma.stock.findUnique({
        where: { productId },
      });

      // 如果库存记录不存在，则创建
      if (!stock) {
        stock = await this.prisma.stock.create({
          data: {
            productId,
            quantity: quantity,
            locked: 0,
          },
        });
      } else {
        // 增加库存
        stock = await this.prisma.stock.update({
          where: { productId },
          data: { quantity: stock.quantity + quantity },
        });
      }

      return {
        code: 200,
        message: '增加成功',
        data: {
          productId: stock.productId,
          quantity: stock.quantity,
          locked: stock.locked,
          available: stock.quantity - stock.locked,
        },
      };
    } catch (error) {
      return { code: 500, message: `增加失败: ${error.message}`, data: null };
    }
  }

  /**
   * 锁定库存
   */
  async lockStock(productId: number, quantity: number) {
    try {
      let stock = await this.prisma.stock.findUnique({
        where: { productId },
      });

      // 如果库存记录不存在，则创建
      if (!stock) {
        stock = await this.prisma.stock.create({
          data: {
            productId,
            quantity: 0,
            locked: 0,
          },
        });
      }

      // 检查可用库存是否充足
      const available = stock.quantity - stock.locked;
      if (available < quantity) {
        return { code: 400, message: '可用库存不足', data: null };
      }

      // 锁定库存
      const updatedStock = await this.prisma.stock.update({
        where: { productId },
        data: { locked: stock.locked + quantity },
      });

      return {
        code: 200,
        message: '锁定成功',
        data: {
          productId: updatedStock.productId,
          quantity: updatedStock.quantity,
          locked: updatedStock.locked,
          available: updatedStock.quantity - updatedStock.locked,
        },
      };
    } catch (error) {
      return { code: 500, message: `锁定失败: ${error.message}`, data: null };
    }
  }

  /**
   * 解锁库存
   */
  async unlockStock(productId: number, quantity: number) {
    try {
      const stock = await this.prisma.stock.findUnique({
        where: { productId },
      });

      if (!stock) {
        return { code: 404, message: '库存记录不存在', data: null };
      }

      // 检查锁定库存是否充足
      if (stock.locked < quantity) {
        return { code: 400, message: '锁定库存不足', data: null };
      }

      // 解锁库存
      const updatedStock = await this.prisma.stock.update({
        where: { productId },
        data: { locked: stock.locked - quantity },
      });

      return {
        code: 200,
        message: '解锁成功',
        data: {
          productId: updatedStock.productId,
          quantity: updatedStock.quantity,
          locked: updatedStock.locked,
          available: updatedStock.quantity - updatedStock.locked,
        },
      };
    } catch (error) {
      return { code: 500, message: `解锁失败: ${error.message}`, data: null };
    }
  }

  /**
   * 导出库存数据
   * @param ids 库存ID数组，为空则导出所有
   * @param res Express Response 对象
   */
  async exportStock(ids: number[] | null, res: Response) {
    try {
      // 1. 查询数据
      const where: any = {};
      if (ids && ids.length > 0) {
        where.productId = { in: ids };
      }

      const stocks = await this.prisma.stock.findMany({
        where,
        orderBy: { productId: 'asc' },
      });

      // 2. 创建 Excel 工作簿
      const workbook = new ExcelJS.Workbook();
      const worksheet = workbook.addWorksheet('库存数据');

      // 3. 创建表头
      worksheet.columns = [
        { header: '商品ID', key: 'productId', width: 12 },
        { header: '总库存', key: 'quantity', width: 10 },
        { header: '锁定库存', key: 'locked', width: 12 },
        { header: '可用库存', key: 'available', width: 12 },
        { header: '创建时间', key: 'createdAt', width: 20 },
        { header: '更新时间', key: 'updatedAt', width: 20 },
      ];

      // 4. 填充数据
      stocks.forEach(stock => {
        worksheet.addRow({
          productId: stock.productId,
          quantity: stock.quantity,
          locked: stock.locked,
          available: stock.quantity - stock.locked,
          createdAt: stock.createdAt ? stock.createdAt.toISOString() : '',
          updatedAt: stock.updatedAt ? stock.updatedAt.toISOString() : '',
        });
      });

      // 5. 设置响应头
      res.setHeader('Content-Type', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
      res.setHeader('Content-Disposition', `attachment; filename=${encodeURIComponent('库存数据.xlsx')}`);

      // 6. 写入响应
      await workbook.xlsx.write(res);
      res.end();
    } catch (error) {
      console.error('导出库存数据失败:', error);
      throw new Error(`导出失败: ${error.message}`);
    }
  }
}
