import { Controller, Post, Body, Get, Req, UseGuards, UnauthorizedException } from '@nestjs/common';
import { AuthService } from './auth.service';
import { LoginDto } from './dto/login.dto';
import { JwtAuthGuard } from './guards/jwt-auth.guard';

@Controller('api/admin')
export class AuthController {
  constructor(private authService: AuthService) {}

  /**
   * 管理员登录
   */
  @Post('login')
  async login(@Body() loginDto: LoginDto) {
    const user = await this.authService.validateAdmin(loginDto.username, loginDto.password);
    if (!user) {
      throw new UnauthorizedException('用户名或密码错误');
    }
    return this.authService.login(user);
  }

  /**
   * 获取当前登录用户信息
   */
  @Get('info')
  @UseGuards(JwtAuthGuard)
  async getUserInfo(@Req() req) {
    return req.user;
  }

  /**
   * 登出
   */
  @Post('logout')
  @UseGuards(JwtAuthGuard)
  async logout() {
    return { code: 200, message: '登出成功', data: null };
  }
}

@Controller('api/customer')
export class CustomerAuthController {
  constructor(private authService: AuthService) {}

  /**
   * 客户登录
   */
  @Post('login')
  async customerLogin(@Body() loginDto: LoginDto) {
    const customer = await this.authService.validateCustomer(loginDto.username, loginDto.password);
    if (!customer) {
      throw new UnauthorizedException('手机号或密码错误');
    }
    return this.authService.customerLogin(customer);
  }

  /**
   * 客户注册
   */
  @Post('register')
  async customerRegister(@Body() registerDto: LoginDto) {
    return this.authService.customerRegister(registerDto);
  }
}
