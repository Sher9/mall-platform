import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { NacosModule } from '../../../libs/common/src/modules/nacos/nacos.module';
import { SeataModule } from '../../../libs/common/src/modules/seata/seata.module';
import { OrderController } from './order.controller';
import { OrderService } from './order.service';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: '../../.env',
    }),
    NacosModule.register('order-service'),
    SeataModule.register('order-service'),
  ],
  controllers: [OrderController],
  providers: [OrderService],
})
export class AppModule {}
