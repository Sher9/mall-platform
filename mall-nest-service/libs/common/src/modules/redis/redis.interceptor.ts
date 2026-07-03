import { Injectable, NestInterceptor, ExecutionContext, CallHandler, SetMetadata } from '@nestjs/common';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { RedisService } from './redis.service';

export const CACHE_KEY_METADATA = 'cache_key';
export const CACHE_TTL_METADATA = 'cache_ttl';

export const Cache = (key: string, ttl?: number) => SetMetadata(CACHE_KEY_METADATA, key);

@Injectable()
export class RedisInterceptor implements NestInterceptor {
  constructor(private redisService: RedisService) {}

  async intercept(context: ExecutionContext, next: CallHandler): Promise<Observable<any>> {
    const request = context.switchToHttp().getRequest();
    const handler = context.getHandler();
    
    const cacheKey = Reflect.getMetadata(CACHE_KEY_METADATA, handler);
    const cacheTtl = Reflect.getMetadata(CACHE_TTL_METADATA, handler);

    // 如果是 GET 请求且有缓存 key，尝试从缓存获取
    if (request.method === 'GET' && cacheKey) {
      const cachedData = await this.redisService.get(cacheKey);
      if (cachedData) {
        return new Observable(observer => {
          observer.next(cachedData);
          observer.complete();
        });
      }
    }

    return next.handle().pipe(
      tap(async (data) => {
        // 如果是 GET 请求且有缓存 key，保存到缓存
        if (request.method === 'GET' && cacheKey && data) {
          await this.redisService.set(cacheKey, data, cacheTtl);
        }
        // 如果是 POST/PUT/DELETE 请求，清除相关缓存
        if (['POST', 'PUT', 'DELETE'].includes(request.method)) {
          const pattern = `${request.route.path}*`;
          await this.redisService.delPattern(pattern);
        }
      })
    );
  }
}
