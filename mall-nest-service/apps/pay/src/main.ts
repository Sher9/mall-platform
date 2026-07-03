import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  const port = process.env.PAY_PORT || 9005;
  await app.listen(port);

  console.log(`✅ Pay 服务启动成功: http://localhost:${port}`);
}
bootstrap();
