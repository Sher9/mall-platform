import { Controller, Post, Get, Put, Body, Param, Query, ParseIntPipe, Res } from '@nestjs/common';
import { Response } from 'express';
import { OrderService } from './order.service';

@Controller('api/order')
export class OrderController {
  constructor(private orderService: OrderService) {}

  /**
   * 创建订单
   * POST /api/order/create
   */
  @Post('create')
  async createOrder(
    @Body('customerId', ParseIntPipe) customerId: number,
    @Body('items') items: any[],
    @Body('address') address: string,
  ) {
    return this.orderService.createOrder(customerId, items, address);
  }

  /**
   * 获取订单列表
   * GET /api/order/list?customerId=1&status=1&pageNum=1&pageSize=10
   */
  @Get('list')
  async getOrderList(
    @Query('customerId', ParseIntPipe) customerId: number,
    @Query('status') status?: string,
    @Query('pageNum', new ParseIntPipe({ optional: true })) pageNum?: number,
    @Query('pageSize', new ParseIntPipe({ optional: true })) pageSize?: number,
  ) {
    return this.orderService.getOrderList(customerId, status ? parseInt(status) : undefined, pageNum || 1, pageSize || 10);
  }

  /**
   * 获取订单详情
   * GET /api/order/:orderNo
   */
  @Get(':orderNo')
  async getOrderDetail(@Param('orderNo') orderNo: string) {
    return this.orderService.getOrderDetail(orderNo);
  }

  /**
   * 取消订单
   * PUT /api/order/cancel
   */
  @Put('cancel')
  async cancelOrder(@Body('orderNo') orderNo: string) {
    return this.orderService.cancelOrder(orderNo);
  }

  /**
   * 支付订单
   * PUT /api/order/pay
   */
  @Put('pay')
  async payOrder(
    @Body('orderNo') orderNo: string,
    @Body('payType', ParseIntPipe) payType: number,
  ) {
    return this.orderService.payOrder(orderNo, payType);
  }

  /**
   * 发货
   * PUT /api/order/ship
   */
  @Put('ship')
  async shipOrder(
    @Body('orderNo') orderNo: string,
    @Body('trackingNo') trackingNo: string,
  ) {
    return this.orderService.shipOrder(orderNo, trackingNo);
  }

  /**
   * 确认收货
   * PUT /api/order/confirm
   */
  @Put('confirm')
  async confirmOrder(@Body('orderNo') orderNo: string) {
    return this.orderService.confirmOrder(orderNo);
  }

  /**
   * 导出订单数据
   * GET /api/order/export?ids=1,2,3 或 /api/order/export（导出所有）
   */
  @Get('export')
  async exportOrder(
    @Query('ids') ids?: string,
    @Res() res?: Response,
  ) {
    return this.orderService.exportOrder(ids ? ids.split(',').map(id => parseInt(id)) : null, res);
  }
}
