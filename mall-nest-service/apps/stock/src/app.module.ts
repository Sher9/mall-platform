import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { NacosModule } from '../../../libs/common/src/modules/nacos/nacos.module';
import { SeataModule } from '../../../libs/common/src/modules/seata/seata.module';
import { StockController } from './stock.controller';
import { StockService } from './stock.service';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: '../../.env',
    }),
    NacosModule.register('stock-service'),
    SeataModule.register('stock-service'),
  ],
  controllers: [StockController],
  providers: [StockService],
})
export class AppModule {}
