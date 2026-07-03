import { Controller, Get, Post, Put, Delete, Body, Param, Query, ParseIntPipe } from '@nestjs/common';
import { AddressService } from './address.service';

@Controller('address')
export class AddressController {
  constructor(private addressService: AddressService) {}

  /**
   * 获取地址列表
   */
  @Get('list')
  async getAddressList(@Query('customerId', ParseIntPipe) customerId: number) {
    return this.addressService.getAddressList(customerId);
  }

  /**
   * 获取地址详情
   */
  @Get(':id')
  async getAddressDetail(@Param('id', ParseIntPipe) id: number) {
    return this.addressService.getAddressDetail(id);
  }

  /**
   * 新增地址
   */
  @Post('add')
  async addAddress(@Body() addressData: any) {
    return this.addressService.addAddress(addressData);
  }

  /**
   * 更新地址
   */
  @Put('update')
  async updateAddress(@Body() addressData: any) {
    return this.addressService.updateAddress(addressData);
  }

  /**
   * 删除地址
   */
  @Delete(':id')
  async deleteAddress(
    @Param('id', ParseIntPipe) id: number,
    @Query('customerId', ParseIntPipe) customerId: number,
  ) {
    return this.addressService.deleteAddress(id, customerId);
  }

  /**
   * 设置默认地址
   */
  @Put('default/:id')
  async setDefault(
    @Param('id', ParseIntPipe) id: number,
    @Query('customerId', ParseIntPipe) customerId: number,
  ) {
    return this.addressService.setDefault(id, customerId);
  }

  /**
   * 获取默认地址
   */
  @Get('default')
  async getDefaultAddress(@Query('customerId', ParseIntPipe) customerId: number) {
    return this.addressService.getDefaultAddress(customerId);
  }
}
