import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  const port = process.env.STOCK_PORT || 9007;
  await app.listen(port);

  console.log(`✅ Stock 服务启动成功: http://localhost:${port}`);
}
bootstrap();
