import { Injectable, OnModuleInit, OnModuleDestroy, Inject } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import * as nacos from 'nacos';

@Injectable()
export class NacosService implements OnModuleInit, OnModuleDestroy {
  private client: any;
  private serviceName: string;

  constructor(
    private configService: ConfigService,
    @Inject('SERVICE_NAME') private readonly serviceNameInject: string,
  ) {
    this.serviceName = serviceNameInject;
  }

  async onModuleInit() {
    try {
      const serverAddr = this.configService.get<string>('NACOS_SERVER_ADDR', 'localhost:8848');
      const namespace = this.configService.get<string>('NACOS_NAMESPACE', 'public');
      const port = this.configService.get<number>('PORT', 9000);

      // 创建 Nacos 客户端
      this.client = new nacos.NacosNamingClient({
        serverList: serverAddr,
        namespace,
        logger: console,
      });

      await this.client.ready();

      // 注册服务
      await this.client.registerInstance(this.serviceName, {
        ip: 'localhost',
        port: port,
        weight: 1,
        metadata: {
          startedAt: new Date().toISOString(),
        },
      });

      console.log(`✅ Nacos 服务注册成功: ${this.serviceName}@localhost:${port}`);

      // 每 5 秒发送一次心跳
      this.startHeartbeat();
    } catch (error) {
      console.error('❌ Nacos 服务注册失败:', error.message);
    }
  }

  async onModuleDestroy() {
    if (this.client) {
      try {
        const port = this.configService.get<number>('PORT', 9000);
        await this.client.deregisterInstance(this.serviceName, {
          ip: 'localhost',
          port: port,
        });
        console.log(`✅ Nacos 服务注销成功: ${this.serviceName}`);
      } catch (error) {
        console.error('❌ Nacos 服务注销失败:', error.message);
      }
    }
  }

  private startHeartbeat() {
    setInterval(async () => {
      try {
        const port = this.configService.get<number>('PORT', 9000);
        await this.client.sendHeartbeat(this.serviceName, {
          ip: 'localhost',
          port: port,
        });
      } catch (error) {
        console.error('❌ Nacos 心跳发送失败:', error.message);
      }
    }, 5000);
  }

  /**
   * 获取服务实例列表
   */
  async getInstances(serviceName: string): Promise<any[]> {
    try {
      const instances = await this.client.getAllInstances(serviceName);
      return instances.filter((instance: any) => instance.healthy);
    } catch (error) {
      console.error(`❌ 获取服务实例失败: ${serviceName}`, error.message);
      return [];
    }
  }
}
