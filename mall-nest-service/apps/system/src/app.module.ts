import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { NacosModule } from '../../../libs/common/src/modules/nacos/nacos.module';
import { RedisModule } from '../../../libs/common/src/modules/redis/redis.module';
import { SeataModule } from '../../../libs/common/src/modules/seata/seata.module';
import { ProductController } from './product.controller';
import { BannerController } from './banner.controller';
import { AddressController } from './address.controller';
import { HomeController } from './home.controller';
import { ProductService } from './product.service';
import { BannerService } from './banner.service';
import { AddressService } from './address.service';
import { CustomerController } from './customer.controller';
import { CustomerService } from './customer.service';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: '../../.env',
    }),
    NacosModule.register('system-service'),
    RedisModule.register(),
    SeataModule.register('system-service'),
  ],
  controllers: [ProductController, BannerController, AddressController, HomeController, CustomerController],
  providers: [ProductService, BannerService, AddressService, CustomerService],
})
export class AppModule {}

