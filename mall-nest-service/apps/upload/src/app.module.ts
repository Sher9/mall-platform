import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { NacosModule } from '../../../libs/common/src/modules/nacos/nacos.module';
import { FileController } from './file.controller';
import { FileService } from './file.service';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: '../../.env',
    }),
    NacosModule.register('upload-service'),
  ],
  controllers: [FileController],
  providers: [FileService],
})
export class AppModule {}

