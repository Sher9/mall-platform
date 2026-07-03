import { Injectable } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';
import * as ExcelJS from 'exceljs';
import { Response } from 'express';

@Injectable()
export class CustomerService {
  private prisma: PrismaClient;

  constructor() {
    this.prisma = new PrismaClient();
  }

  /**
   * 获取客户列表
   */
  async getCustomerList(pageNum: number = 1, pageSize: number = 10, name?: string) {
    try {
      const skip = (pageNum - 1) * pageSize;
      const where: any = {};
      if (name) {
        where.name = { contains: name };
      }

      const [records, total] = await Promise.all([
        this.prisma.customer.findMany({
          where,
          skip,
          take: pageSize,
          orderBy: { createTime: 'desc' },
        }),
        this.prisma.customer.count({ where }),
      ]);

      return {
        code: 200,
        message: '获取成功',
        data: {
          records,
          total,
          size: pageSize,
          current: pageNum,
          pages: Math.ceil(total / pageSize),
        },
      };
    } catch (error) {
      return { code: 500, message: `获取失败: ${error.message}`, data: null };
    }
  }

  /**
   * 获取客户详情
   */
  async getCustomerDetail(id: number) {
    try {
      const customer = await this.prisma.customer.findUnique({
        where: { id },
      });

      if (!customer) {
        return { code: 404, message: '客户不存在', data: null };
      }

      return { code: 200, message: '获取成功', data: customer };
    } catch (error) {
      return { code: 500, message: `获取失败: ${error.message}`, data: null };
    }
  }

  /**
   * 创建客户
   */
  async createCustomer(customerData: any) {
    try {
      const customer = await this.prisma.customer.create({
        data: {
          username: customerData.username,
          nickname: customerData.nickname,
          gender: customerData.gender || 0,
          phone: customerData.phone,
          email: customerData.email,
          birthday: customerData.birthday,
          status: customerData.status || 1,
        },
      });

      return { code: 200, message: '创建成功', data: customer };
    } catch (error) {
      return { code: 500, message: `创建失败: ${error.message}`, data: null };
    }
  }

  /**
   * 更新客户
   */
  async updateCustomer(customerData: any) {
    try {
      const customer = await this.prisma.customer.update({
        where: { id: customerData.id },
        data: {
          nickname: customerData.nickname,
          gender: customerData.gender,
          phone: customerData.phone,
          email: customerData.email,
          birthday: customerData.birthday,
          status: customerData.status,
        },
      });

      return { code: 200, message: '更新成功', data: customer };
    } catch (error) {
      return { code: 500, message: `更新失败: ${error.message}`, data: null };
    }
  }

  /**
   * 删除客户
   */
  async deleteCustomer(id: number) {
    try {
      await this.prisma.customer.delete({
        where: { id },
      });

      return { code: 200, message: '删除成功', data: null };
    } catch (error) {
      return { code: 500, message: `删除失败: ${error.message}`, data: null };
    }
  }

  /**
   * 导出客户数据
   * @param ids 客户ID数组，为空则导出所有
   * @param res Express Response 对象
   */
  async exportCustomer(ids: number[] | null, res: Response) {
    try {
      // 1. 查询数据
      const where: any = {};
      if (ids && ids.length > 0) {
        where.id = { in: ids };
      }

      const customers = await this.prisma.customer.findMany({
        where,
        orderBy: { createTime: 'desc' },
      });

      // 2. 创建 Excel 工作簿
      const workbook = new ExcelJS.Workbook();
      const worksheet = workbook.addWorksheet('客户数据');

      // 3. 创建表头
      worksheet.columns = [
        { header: 'ID', key: 'id', width: 10 },
        { header: '用户名', key: 'username', width: 20 },
        { header: '昵称', key: 'nickname', width: 20 },
        { header: '性别', key: 'gender', width: 10 },
        { header: '手机号', key: 'phone', width: 15 },
        { header: '邮箱', key: 'email', width: 25 },
        { header: '生日', key: 'birthday', width: 15 },
        { header: '状态', key: 'status', width: 10 },
        { header: '注册时间', key: 'createTime', width: 20 },
        { header: '最后登录时间', key: 'lastLoginTime', width: 20 },
      ];

      // 4. 填充数据
      customers.forEach(customer => {
        worksheet.addRow({
          id: customer.id,
          username: customer.username,
          nickname: customer.nickname,
          gender: customer.gender,
          phone: customer.phone,
          email: customer.email,
          birthay: customer.birthday ? customer.birthday.toISOString() : '',
          status: customer.status,
          createTime: customer.createTime ? customer.createTime.toISOString() : '',
          lastLoginTime: customer.lastLoginTime ? customer.lastLoginTime.toISOString() : '',
        });
      });

      // 5. 设置响应头
      res.setHeader('Content-Type', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
      res.setHeader('Content-Disposition', `attachment; filename=${encodeURIComponent('客户数据.xlsx')}`);

      // 6. 写入响应
      await workbook.xlsx.write(res);
      res.end();
    } catch (error) {
      console.error('导出客户数据失败:', error);
      throw new Error(`导出失败: ${error.message}`);
    }
  }
}
