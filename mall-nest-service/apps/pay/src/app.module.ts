import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { NacosModule } from '../../../libs/common/src/modules/nacos/nacos.module';
import { SeataModule } from '../../../libs/common/src/modules/seata/seata.module';
import { PayController } from './pay.controller';
import { PayService } from './pay.service';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: '../../.env',
    }),
    NacosModule.register('pay-service'),
    SeataModule.register('pay-service'),
  ],
  controllers: [PayController],
  providers: [PayService],
})
export class AppModule {}
