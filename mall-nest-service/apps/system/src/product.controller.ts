import { Controller, Get, Post, Put, Delete, Body, Param, Query, ParseIntPipe } from '@nestjs/common';
import { ProductService } from './product.service';

@Controller('product')
export class ProductController {
  constructor(private productService: ProductService) {}

  /**
   * 获取商品列表（分页）
   */
  @Get()
  async getProductList(
    @Query('pageNum', new ParseIntPipe({ optional: true })) pageNum: number = 1,
    @Query('pageSize', new ParseIntPipe({ optional: true })) pageSize: number = 10,
    @Query('productName') productName?: string,
  ) {
    return this.productService.getProductList(pageNum, pageSize, productName);
  }

  /**
   * 获取商品详情
   */
  @Get(':id')
  async getProductDetail(@Param('id', ParseIntPipe) id: number) {
    return this.productService.getProductDetail(id);
  }

  /**
   * 新增商品
   */
  @Post()
  async addProduct(@Body() productData: any) {
    return this.productService.addProduct(productData);
  }

  /**
   * 更新商品
   */
  @Put()
  async updateProduct(@Body() productData: any) {
    return this.productService.updateProduct(productData);
  }

  /**
   * 删除商品
   */
  @Delete(':id')
  async deleteProduct(@Param('id', ParseIntPipe) id: number) {
    return this.productService.deleteProduct(id);
  }
}
