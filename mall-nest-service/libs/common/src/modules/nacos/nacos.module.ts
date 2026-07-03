import { DynamicModule, Module, Provider } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { NacosService } from './nacos.service';

@Module({})
export class NacosModule {
  static register(serviceName: string): DynamicModule {
    return {
      module: NacosModule,
      imports: [ConfigModule],
      providers: [
        {
          provide: 'SERVICE_NAME',
          useValue: serviceName,
        },
        NacosService,
      ],
      exports: [NacosService],
    };
  }
}
