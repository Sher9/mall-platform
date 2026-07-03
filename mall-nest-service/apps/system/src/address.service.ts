import { Injectable } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';

@Injectable()
export class AddressService {
  private prisma: PrismaClient;

  constructor() {
    this.prisma = new PrismaClient();
  }

  /**
   * 获取地址列表
   */
  async getAddressList(customerId: number) {
    const addresses = await this.prisma.address.findMany({
      where: { customerId },
      orderBy: [{ isDefault: 'desc' }, { createdAt: 'desc' }],
    });
    return {
      code: 200,
      message: '获取成功',
      data: addresses,
    };
  }

  /**
   * 获取地址详情
   */
  async getAddressDetail(id: number) {
    const address = await this.prisma.address.findUnique({
      where: { id },
    });
    return {
      code: 200,
      message: '获取成功',
      data: address,
    };
  }

  /**
   * 新增地址
   */
  async addAddress(addressData: any) {
    // 如果设置为默认地址，先取消其他默认地址
    if (addressData.isDefault) {
      await this.prisma.address.updateMany({
        where: { customerId: addressData.customerId },
        data: { isDefault: false },
      });
    }

    const address = await this.prisma.address.create({
      data: {
        customerId: addressData.customerId,
        name: addressData.name,
        phone: addressData.phone,
        province: addressData.province,
        city: addressData.city,
        district: addressData.district,
        detail: addressData.detail,
        isDefault: addressData.isDefault || false,
      },
    });
    return {
      code: 200,
      message: '新增成功',
      data: address,
    };
  }

  /**
   * 更新地址
   */
  async updateAddress(addressData: any) {
    // 如果设置为默认地址，先取消其他默认地址
    if (addressData.isDefault) {
      await this.prisma.address.updateMany({
        where: { customerId: addressData.customerId },
        data: { isDefault: false },
      });
    }

    const address = await this.prisma.address.update({
      where: { id: addressData.id },
      data: {
        name: addressData.name,
        phone: addressData.phone,
        province: addressData.province,
        city: addressData.city,
        district: addressData.district,
        detail: addressData.detail,
        isDefault: addressData.isDefault,
      },
    });
    return {
      code: 200,
      message: '更新成功',
      data: address,
    };
  }

  /**
   * 删除地址
   */
  async deleteAddress(id: number, customerId: number) {
    await this.prisma.address.deleteMany({
      where: { id, customerId },
    });
    return {
      code: 200,
      message: '删除成功',
      data: null,
    };
  }

  /**
   * 设置默认地址
   */
  async setDefault(id: number, customerId: number) {
    // 先取消其他默认地址
    await this.prisma.address.updateMany({
      where: { customerId },
      data: { isDefault: false },
    });

    // 设置新的默认地址
    await this.prisma.address.update({
      where: { id, customerId },
      data: { isDefault: true },
    });

    return {
      code: 200,
      message: '设置成功',
      data: null,
    };
  }

  /**
   * 获取默认地址
   */
  async getDefaultAddress(customerId: number) {
    const address = await this.prisma.address.findFirst({
      where: { customerId, isDefault: true },
    });
    return {
      code: 200,
      message: '获取成功',
      data: address,
    };
  }
}
