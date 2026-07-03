import { Controller, Get } from '@nestjs/common';

@Controller()
export class GatewayController {
  @Get('health')
  healthCheck() {
    return { status: 'ok', timestamp: new Date().toISOString() };
  }
}
