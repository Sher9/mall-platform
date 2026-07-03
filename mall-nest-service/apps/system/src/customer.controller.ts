import { Controller, Get, Post, Put, Delete, Body, Param, Query, ParseIntPipe, Res } from '@nestjs/common';
import { Response } from 'express';
import { CustomerService } from './customer.service';

@Controller('api/customers')
export class CustomerController {
  constructor(private customerService: CustomerService) {}

  /**
   * 获取客户列表
   * GET /api/customers?pageNum=1&pageSize=10&name=test
   */
  @Get()
  async getCustomerList(
    @Query('pageNum', new ParseIntPipe({ optional: true })) pageNum?: number,
    @Query('pageSize', new ParseIntPipe({ optional: true })) pageSize?: number,
    @Query('name') name?: string,
  ) {
    return this.customerService.getCustomerList(pageNum || 1, pageSize || 10, name);
  }

  /**
   * 获取客户详情
   * GET /api/customers/:id
   */
  @Get(':id')
  async getCustomerDetail(@Param('id', ParseIntPipe) id: number) {
    return this.customerService.getCustomerDetail(id);
  }

  /**
   * 创建客户
   * POST /api/customers
   */
  @Post()
  async createCustomer(@Body() customerData: any) {
    return this.customerService.createCustomer(customerData);
  }

  /**
   * 更新客户
   * PUT /api/customers
   */
  @Put()
  async updateCustomer(@Body() customerData: any) {
    return this.customerService.updateCustomer(customerData);
  }

  /**
   * 删除客户
   * DELETE /api/customers/:id
   */
  @Delete(':id')
  async deleteCustomer(@Param('id', ParseIntPipe) id: number) {
    return this.customerService.deleteCustomer(id);
  }

  /**
   * 导出客户数据
   * GET /api/customers/export?ids=1,2,3 或 /api/customers/export（导出所有）
   */
  @Get('export')
  async exportCustomer(
    @Query('ids') ids?: string,
    @Res() res?: Response,
  ) {
    return this.customerService.exportCustomer(ids ? ids.split(',').map(id => parseInt(id)) : null, res);
  }
}
