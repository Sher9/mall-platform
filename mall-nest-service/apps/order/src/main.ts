import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  const port = process.env.ORDER_PORT || 9006;
  await app.listen(port);

  console.log(`✅ Order 服务启动成功: http://localhost:${port}`);
}
bootstrap();
