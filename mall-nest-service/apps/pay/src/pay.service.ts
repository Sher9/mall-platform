import { Injectable } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';

@Injectable()
export class PayService {
  private prisma: PrismaClient;

  constructor() {
    this.prisma = new PrismaClient();
  }

  /**
   * 创建支付订单
   */
  async createPayOrder(orderNo: string, customerId: number, amount: number, payType: number) {
    try {
      // 检查是否已存在支付订单
      const existingPayOrder = await this.prisma.payOrder.findUnique({
        where: { orderNo },
      });

      if (existingPayOrder) {
        return { code: 200, message: '支付订单已存在', data: existingPayOrder };
      }

      // 创建支付订单
      const payOrder = await this.prisma.payOrder.create({
        data: {
          payNo: `PAY${Date.now()}${Math.random().toString(36).substring(2, 6).toUpperCase()}`,
          orderNo,
          customerId,
          amount,
          payType,
          status: 0, // 待支付
        },
      });

      return { code: 200, message: '创建成功', data: payOrder };
    } catch (error) {
      return { code: 500, message: `创建失败: ${error.message}`, data: null };
    }
  }

  /**
   * 微信支付下单（模拟）
   */
  async wechatPay(orderNo: string, amount: number, openid: string) {
    try {
      // 模拟调用微信支付统一下单接口
      const prepayId = `prepay_${Date.now()}_${Math.random().toString(36).substring(2, 9)}`;

      return {
        code: 200,
        message: '下单成功',
        data: {
          orderNo,
          prepayId,
          amount,
          // 实际项目中需要返回微信支付参数（appId、timeStamp、nonceStr、package、signType、paySign）
        },
      };
    } catch (error) {
      return { code: 500, message: `下单失败: ${error.message}`, data: null };
    }
  }

  /**
   * 支付宝支付下单（模拟）
   */
  async alipay(orderNo: string, amount: number, returnUrl: string) {
    try {
      // 模拟调用支付宝支付接口
      const payForm = `<form action="https://openapi.alipay.com/gateway.do" method="POST">
        <input type="hidden" name="orderNo" value="${orderNo}">
        <input type="hidden" name="amount" value="${amount}">
      </form>`;

      return {
        code: 200,
        message: '下单成功',
        data: {
          orderNo,
          amount,
          payForm, // 实际项目中需要返回支付宝支付表单
        },
      };
    } catch (error) {
      return { code: 500, message: `下单失败: ${error.message}`, data: null };
    }
  }

  /**
   * 微信支付回调（模拟）
   */
  async wechatCallback(callbackData: any) {
    try {
      // 模拟处理微信支付回调
      const { transaction_id, out_trade_no, result_code } = callbackData;

      if (result_code === 'SUCCESS') {
        // 更新支付订单状态
        await this.prisma.payOrder.update({
          where: { payNo: out_trade_no },
          data: {
            status: 1, // 已支付
            transactionId: transaction_id,
            payTime: new Date(),
          },
        });

        // 更新订单状态
        await this.prisma.order.update({
          where: { orderNo: out_trade_no },
          data: {
            status: 1, // 已支付
            payTime: new Date(),
          },
        });

        return { code: 200, message: '处理成功', data: null };
      } else {
        return { code: 400, message: '支付失败', data: null };
      }
    } catch (error) {
      return { code: 500, message: `处理失败: ${error.message}`, data: null };
    }
  }

  /**
   * 支付宝支付回调（模拟）
   */
  async alipayCallback(callbackData: any) {
    try {
      // 模拟处理支付宝回调
      const { trade_no, out_trade_no, trade_status } = callbackData;

      if (trade_status === 'TRADE_SUCCESS') {
        // 更新支付订单状态
        await this.prisma.payOrder.update({
          where: { payNo: out_trade_no },
          data: {
            status: 1, // 已支付
            transactionId: trade_no,
            payTime: new Date(),
          },
        });

        // 更新订单状态
        await this.prisma.order.update({
          where: { orderNo: out_trade_no },
          data: {
            status: 1, // 已支付
            payTime: new Date(),
          },
        });

        return { code: 200, message: '处理成功', data: null };
      } else {
        return { code: 400, message: '支付失败', data: null };
      }
    } catch (error) {
      return { code: 500, message: `处理失败: ${error.message}`, data: null };
    }
  }

  /**
   * 查询支付状态
   */
  async getPayStatus(payNo: string) {
    try {
      const payOrder = await this.prisma.payOrder.findUnique({
        where: { payNo },
      });

      if (!payOrder) {
        return { code: 404, message: '支付订单不存在', data: null };
      }

      return {
        code: 200,
        message: '查询成功',
        data: {
          payNo: payOrder.payNo,
          orderNo: payOrder.orderNo,
          amount: payOrder.amount,
          payType: payOrder.payType,
          status: payOrder.status,
          payTime: payOrder.payTime,
        },
      };
    } catch (error) {
      return { code: 500, message: `查询失败: ${error.message}`, data: null };
    }
  }

  /**
   * 关闭支付订单
   */
  async closePayOrder(payNo: string) {
    try {
      const payOrder = await this.prisma.payOrder.findUnique({
        where: { payNo },
      });

      if (!payOrder) {
        return { code: 404, message: '支付订单不存在', data: null };
      }

      if (payOrder.status !== 0) {
        return { code: 400, message: '支付订单状态不允许关闭', data: null };
      }

      // 更新支付订单状态
      await this.prisma.payOrder.update({
        where: { payNo },
        data: { status: 2 }, // 已关闭
      });

      return { code: 200, message: '关闭成功', data: null };
    } catch (error) {
      return { code: 500, message: `关闭失败: ${error.message}`, data: null };
    }
  }

  /**
   * 退款（模拟）
   */
  async refund(payNo: string, refundAmount: number, reason: string) {
    try {
      const payOrder = await this.prisma.payOrder.findUnique({
        where: { payNo },
      });

      if (!payOrder) {
        return { code: 404, message: '支付订单不存在', data: null };
      }

      if (payOrder.status !== 1) {
        return { code: 400, message: '支付订单状态不允许退款', data: null };
      }

      if (refundAmount > payOrder.amount) {
        return { code: 400, message: '退款金额不能大于支付金额', data: null };
      }

      // 模拟调用退款接口
      const refundNo = `REF${Date.now()}${Math.random().toString(36).substring(2, 6).toUpperCase()}`;

      // 更新支付订单状态
      await this.prisma.payOrder.update({
        where: { payNo },
        data: { status: 3 }, // 已退款
      });

      return {
        code: 200,
        message: '退款成功',
        data: {
          refundNo,
          payNo,
          refundAmount,
          reason,
        },
      };
    } catch (error) {
      return { code: 500, message: `退款失败: ${error.message}`, data: null };
    }
  }
}
