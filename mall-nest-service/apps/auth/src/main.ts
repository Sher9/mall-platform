import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  
  // 启用 CORS
  app.enableCors();
  
  // 获取配置端口
  const port = process.env.AUTH_PORT || 8002;
  
  await app.listen(port);
  console.log(`Auth service is running on: http://localhost:${port}`);
}
bootstrap();
