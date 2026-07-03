import { Module, NestModule, MiddlewareConsumer } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { NacosModule } from '../../libs/common/src/modules/nacos/nacos.module';
import { GatewayController } from './gateway.controller';
import { GatewayService } from './gateway.service';
import { ProxyMiddleware } from './proxy.middleware';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: '../../.env',
    }),
    NacosModule.register('gateway'),
  ],
  controllers: [GatewayController],
  providers: [GatewayService],
})
export class AppModule implements NestModule {
  configure(consumer: MiddlewareConsumer) {
    consumer
      .apply(ProxyMiddleware)
      .forRoutes('*');
  }
}

