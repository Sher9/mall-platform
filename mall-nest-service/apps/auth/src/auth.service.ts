import { Injectable } from '@nestjs/common';
import { PrismaClient } from '@prisma/client';
import * as bcrypt from 'bcrypt';
import { JwtService } from '@nestjs/jwt';

@Injectable()
export class AuthService {
  private prisma: PrismaClient;

  constructor(private jwtService: JwtService) {
    this.prisma = new PrismaClient();
  }

  /**
   * 验证管理员
   */
  async validateAdmin(username: string, password: string): Promise<any> {
    const user = await this.prisma.adminUser.findUnique({
      where: { username },
    });

    if (user && await bcrypt.compare(password, user.password)) {
      const { password, ...result } = user;
      return result;
    }
    return null;
  }

  /**
   * 验证客户
   */
  async validateCustomer(username: string, password: string): Promise<any> {
    const customer = await this.prisma.customer.findUnique({
      where: { username },
    });

    if (customer && await bcrypt.compare(password, customer.password)) {
      const { password, ...result } = customer;
      return result;
    }
    return null;
  }

  /**
   * 管理员登录
   */
  async login(user: any) {
    const payload = { username: user.username, sub: user.id, roles: ['ADMIN'] };
    return {
      code: 200,
      message: '登录成功',
      data: {
        accessToken: this.jwtService.sign(payload),
        tokenType: 'Bearer',
        userId: user.id,
        username: user.username,
        nickname: user.nickname,
        avatar: user.avatar,
        email: user.email,
        phone: user.phone,
        roles: ['ADMIN'],
        permissions: ['*'],
      },
    };
  }

  /**
   * 客户登录
   */
  async customerLogin(customer: any) {
    const payload = { username: customer.username, sub: customer.id, roles: ['CUSTOMER'] };
    return {
      code: 200,
      message: '登录成功',
      data: {
        accessToken: this.jwtService.sign(payload),
        tokenType: 'Bearer',
        userId: customer.id,
        username: customer.username,
        nickname: customer.nickname,
        avatar: customer.avatar,
        email: customer.email,
        phone: customer.phone,
        roles: ['CUSTOMER'],
        permissions: ['shop:view'],
      },
    };
  }

  /**
   * 客户注册
   */
  async customerRegister(registerDto: any) {
    const hashedPassword = await bcrypt.hash(registerDto.password, 10);
    const customer = await this.prisma.customer.create({
      data: {
        username: registerDto.username,
        password: hashedPassword,
        nickname: registerDto.nickname || '新用户',
      },
    });
    const { password, ...result } = customer;
    return {
      code: 200,
      message: '注册成功',
      data: result,
    };
  }
}
