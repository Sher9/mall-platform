import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { NacosModule } from '../../../libs/common/src/modules/nacos/nacos.module';
import { RedisModule } from '../../../libs/common/src/modules/redis/redis.module';
import { CartController } from './cart.controller';
import { CartService } from './cart.service';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: '../../.env',
    }),
    NacosModule.register('cart-service'),
    RedisModule.register(),
  ],
  controllers: [CartController],
  providers: [CartService],
})
export class AppModule {}
