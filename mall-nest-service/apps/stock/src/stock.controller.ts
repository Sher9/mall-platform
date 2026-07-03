import { Controller, Get, Put, Post, Body, Param, ParseIntPipe, Query, Res } from '@nestjs/common';
import { Response } from 'express';
import { StockService } from './stock.service';

@Controller('api/stock')
export class StockController {
  constructor(private stockService: StockService) {}

  /**
   * 查询库存
   * GET /api/stock/:productId
   */
  @Get(':productId')
  async getStock(@Param('productId', ParseIntPipe) productId: number) {
    return this.stockService.getStock(productId);
  }

  /**
   * 扣减库存
   * PUT /api/stock/deduct
   */
  @Put('deduct')
  async deductStock(
    @Body('productId', ParseIntPipe) productId: number,
    @Body('quantity', ParseIntPipe) quantity: number,
  ) {
    return this.stockService.deductStock(productId, quantity);
  }

  /**
   * 批量扣减库存
   * PUT /api/stock/batchDeduct
   */
  @Put('batchDeduct')
  async batchDeduct(@Body('items') items: { productId: number; quantity: number }[]) {
    return this.stockService.batchDeduct(items);
  }

  /**
   * 增加库存
   * PUT /api/stock/add
   */
  @Put('add')
  async addStock(
    @Body('productId', ParseIntPipe) productId: number,
    @Body('quantity', ParseIntPipe) quantity: number,
  ) {
    return this.stockService.addStock(productId, quantity);
  }

  /**
   * 锁定库存
   * POST /api/stock/lock
   */
  @Post('lock')
  async lockStock(
    @Body('productId', ParseIntPipe) productId: number,
    @Body('quantity', ParseIntPipe) quantity: number,
  ) {
    return this.stockService.lockStock(productId, quantity);
  }

  /**
   * 解锁库存
   * POST /api/stock/unlock
   */
  @Post('unlock')
  async unlockStock(
    @Body('productId', ParseIntPipe) productId: number,
    @Body('quantity', ParseIntPipe) quantity: number,
  ) {
    return this.stockService.unlockStock(productId, quantity);
  }

  /**
   * 导出库存数据
   * GET /api/stock/export?ids=1,2,3 或 /api/stock/export（导出所有）
   */
  @Get('export')
  async exportStock(
    @Query('ids') ids?: string,
    @Res() res?: Response,
  ) {
    return this.stockService.exportStock(ids ? ids.split(',').map(id => parseInt(id)) : null, res);
  }
}
