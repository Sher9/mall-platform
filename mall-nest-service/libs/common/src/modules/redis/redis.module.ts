import { Module, DynamicModule } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { RedisService } from './redis.service';
import { RedisInterceptor } from './redis.interceptor';

@Module({})
export class RedisModule {
  static register(): DynamicModule {
    return {
      module: RedisModule,
      imports: [ConfigModule],
      providers: [RedisService, RedisInterceptor],
      exports: [RedisService, RedisInterceptor],
    };
  }
}
