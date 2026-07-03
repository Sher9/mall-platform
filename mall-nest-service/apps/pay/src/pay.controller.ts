import { Controller, Post, Get, Put, Body, Query, ParseIntPipe } from '@nestjs/common';
import { PayService } from './pay.service';

@Controller('api/pay')
export class PayController {
  constructor(private payService: PayService) {}

  /**
   * 创建支付订单
   * POST /api/pay/create
   */
  @Post('create')
  async createPayOrder(
    @Body('orderNo') orderNo: string,
    @Body('customerId', ParseIntPipe) customerId: number,
    @Body('amount', ParseIntPipe) amount: number,
    @Body('payType', ParseIntPipe) payType: number,
  ) {
    return this.payService.createPayOrder(orderNo, customerId, amount, payType);
  }

  /**
   * 微信支付下单
   * POST /api/pay/wechat
   */
  @Post('wechat')
  async wechatPay(
    @Body('orderNo') orderNo: string,
    @Body('amount', ParseIntPipe) amount: number,
    @Body('openid') openid: string,
  ) {
    return this.payService.wechatPay(orderNo, amount, openid);
  }

  /**
   * 支付宝支付下单
   * POST /api/pay/alipay
   */
  @Post('alipay')
  async alipay(
    @Body('orderNo') orderNo: string,
    @Body('amount', ParseIntPipe) amount: number,
    @Body('returnUrl') returnUrl: string,
  ) {
    return this.payService.alipay(orderNo, amount, returnUrl);
  }

  /**
   * 微信支付回调
   * POST /api/pay/wechat/callback
   */
  @Post('wechat/callback')
  async wechatCallback(@Body() callbackData: any) {
    return this.payService.wechatCallback(callbackData);
  }

  /**
   * 支付宝支付回调
   * POST /api/pay/alipay/callback
   */
  @Post('alipay/callback')
  async alipayCallback(@Body() callbackData: any) {
    return this.payService.alipayCallback(callbackData);
  }

  /**
   * 查询支付状态
   * GET /api/pay/status?payNo=xxx
   */
  @Get('status')
  async getPayStatus(@Query('payNo') payNo: string) {
    return this.payService.getPayStatus(payNo);
  }

  /**
   * 关闭支付订单
   * POST /api/pay/close
   */
  @Post('close')
  async closePayOrder(@Body('payNo') payNo: string) {
    return this.payService.closePayOrder(payNo);
  }

  /**
   * 退款
   * POST /api/pay/refund
   */
  @Post('refund')
  async refund(
    @Body('payNo') payNo: string,
    @Body('refundAmount', ParseIntPipe) refundAmount: number,
    @Body('reason') reason: string,
  ) {
    return this.payService.refund(payNo, refundAmount, reason);
  }
}
