import { DynamicModule, Module, Provider } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { SeataService } from './seata.service';

@Module({})
export class SeataModule {
  static register(serviceName: string): DynamicModule {
    return {
      module: SeataModule,
      imports: [ConfigModule],
      providers: [
        {
          provide: 'SEATA_SERVICE_NAME',
          useValue: serviceName,
        },
        SeataService,
      ],
      exports: [SeataService],
    };
  }
}
