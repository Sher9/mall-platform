import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  const port = process.env.CART_PORT || 9004;
  await app.listen(port);

  console.log(`✅ Cart 服务启动成功: http://localhost:${port}`);
}
bootstrap();
