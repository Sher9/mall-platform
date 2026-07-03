import { Controller, Post, Get, Put, Delete, Body, Query, Param, ParseIntPipe } from '@nestjs/common';
import { CartService } from './cart.service';

@Controller('api/cart')
export class CartController {
  constructor(private cartService: CartService) {}

  /**
   * 添加商品到购物车
   * POST /api/cart/add
   */
  @Post('add')
  async addToCart(
    @Body('customerId', ParseIntPipe) customerId: number,
    @Body('productId', ParseIntPipe) productId: number,
    @Body('quantity', ParseIntPipe) quantity: number,
    @Body('skuId') skuId?: number,
  ) {
    return this.cartService.addToCart(customerId, productId, quantity, skuId);
  }

  /**
   * 获取购物车列表
   * GET /api/cart/list?customerId=1
   */
  @Get('list')
  async getCartList(@Query('customerId', ParseIntPipe) customerId: number) {
    return this.cartService.getCartList(customerId);
  }

  /**
   * 更新商品数量
   * PUT /api/cart/quantity
   */
  @Put('quantity')
  async updateQuantity(
    @Body('cartItemId', ParseIntPipe) cartItemId: number,
    @Body('quantity', ParseIntPipe) quantity: number,
  ) {
    return this.cartService.updateQuantity(cartItemId, quantity);
  }

  /**
   * 删除购物车商品
   * DELETE /api/cart/:cartItemId?customerId=1
   */
  @Delete(':cartItemId')
  async deleteCartItem(
    @Param('cartItemId', ParseIntPipe) cartItemId: number,
    @Query('customerId', ParseIntPipe) customerId: number,
  ) {
    return this.cartService.deleteCartItem(customerId, cartItemId);
  }

  /**
   * 批量删除购物车商品
   * DELETE /api/cart/batch?customerId=1&cartItemIds=1,2,3
   */
  @Delete('batch')
  async batchDelete(
    @Query('customerId', ParseIntPipe) customerId: number,
    @Query('cartItemIds') cartItemIds: string,
  ) {
    const ids = cartItemIds.split(',').map(id => parseInt(id));
    return this.cartService.batchDelete(customerId, ids);
  }

  /**
   * 更新选中状态
   * PUT /api/cart/selected
   */
  @Put('selected')
  async updateSelected(
    @Body('cartItemId', ParseIntPipe) cartItemId: number,
    @Body('selected') selected: boolean,
    @Body('customerId', ParseIntPipe) customerId: number,
  ) {
    return this.cartService.updateSelected(customerId, cartItemId, selected);
  }

  /**
   * 全选/取消全选
   * PUT /api/cart/selectAll?customerId=1&selected=true
   */
  @Put('selectAll')
  async selectAll(
    @Query('customerId', ParseIntPipe) customerId: number,
    @Query('selected') selected: boolean,
  ) {
    return this.cartService.selectAll(customerId, selected);
  }

  /**
   * 获取购物车数量
   * GET /api/cart/count?customerId=1
   */
  @Get('count')
  async getCartCount(@Query('customerId', ParseIntPipe) customerId: number) {
    return this.cartService.getCartCount(customerId);
  }
}
