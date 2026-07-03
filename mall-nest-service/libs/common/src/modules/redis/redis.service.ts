import { Injectable, OnModuleInit, OnModuleDestroy } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import * as Redis from 'ioredis';

@Injectable()
export class RedisService implements OnModuleInit, OnModuleDestroy {
  private client: Redis;

  constructor(private configService: ConfigService) {}

  async onModuleInit() {
    try {
      this.client = new Redis({
        host: this.configService.get<string>('REDIS_HOST', 'localhost'),
        port: this.configService.get<number>('REDIS_PORT', 6379),
        password: this.configService.get<string>('REDIS_PASSWORD', '123456'),
        db: this.configService.get<number>('REDIS_DATABASE', 0),
        retryDelayOnFailover: 100,
        maxRetriesPerRequest: 3,
      });

      this.client.on('connect', () => {
        console.log('✅ Redis 连接成功');
      });

      this.client.on('error', (error) => {
        console.error('❌ Redis 连接错误:', error.message);
      });

      // 测试连接
      await this.client.ping();
    } catch (error) {
      console.error('❌ Redis 初始化失败:', error.message);
    }
  }

  async onModuleDestroy() {
    if (this.client) {
      await this.client.quit();
      console.log('✅ Redis 连接已关闭');
    }
  }

  /**
   * 设置缓存
   */
  async set(key: string, value: any, ttl?: number): Promise<void> {
    try {
      const serializedValue = JSON.stringify(value);
      if (ttl) {
        await this.client.setex(key, ttl, serializedValue);
      } else {
        await this.client.set(key, serializedValue);
      }
    } catch (error) {
      console.error(`❌ Redis SET 失败: ${key}`, error.message);
    }
  }

  /**
   * 获取缓存
   */
  async get<T = any>(key: string): Promise<T | null> {
    try {
      const value = await this.client.get(key);
      if (value) {
        return JSON.parse(value) as T;
      }
      return null;
    } catch (error) {
      console.error(`❌ Redis GET 失败: ${key}`, error.message);
      return null;
    }
  }

  /**
   * 删除缓存
   */
  async del(key: string): Promise<void> {
    try {
      await this.client.del(key);
    } catch (error) {
      console.error(`❌ Redis DEL 失败: ${key}`, error.message);
    }
  }

  /**
   * 批量删除缓存（支持通配符）
   */
  async delPattern(pattern: string): Promise<void> {
    try {
      const keys = await this.client.keys(pattern);
      if (keys.length > 0) {
        await this.client.del(...keys);
      }
    } catch (error) {
      console.error(`❌ Redis DEL PATTERN 失败: ${pattern}`, error.message);
    }
  }

  /**
   * 设置过期时间
   */
  async expire(key: string, ttl: number): Promise<void> {
    try {
      await this.client.expire(key, ttl);
    } catch (error) {
      console.error(`❌ Redis EXPIRE 失败: ${key}`, error.message);
    }
  }

  /**
   * 获取原始客户端（用于高级操作）
   */
  getClient(): Redis {
    return this.client;
  }
}
