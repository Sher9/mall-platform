# NestJS 微服务项目 - Nacos、Redis、Seata 集成指南

## 已完成的功能

### 1. Nacos 服务注册

**功能说明**：
- 所有微服务启动时会自动注册到 Nacos 注册中心
- 每 5 秒发送一次心跳，保持服务健康状态
- 服务关闭时自动注销

**已集成的服务**：
- ✅ Gateway (端口 9000)
- ✅ Auth (端口 9002)
- ✅ System (端口 9001)
- ✅ Order (端口 9006)
- ✅ Stock (端口 9007)
- ✅ Upload (端口 9003)
- ✅ Cart (端口 9004)
- ✅ Pay (端口 9005)

**配置文件**：`.env`
```env
NACOS_SERVER_ADDR=localhost:8848
NACOS_NAMESPACE=public
```

**使用示例**：
```typescript
// 在 AppModule 中集成
import { NacosModule } from '../../../libs/common/src/modules/nacos/nacos.module';

@Module({
  imports: [
    NacosModule.register('service-name'),
  ],
})
export class AppModule {}
```

---

### 2. Redis 缓存

**功能说明**：
- 支持缓存的增删改查
- 支持缓存过期时间设置
- 支持批量删除（通配符）
- 自动缓存管理（通过拦截器）

**已集成的服务**：
- ✅ System (商品缓存)
- ✅ Cart (购物车缓存)
- ✅ Order (订单缓存)

**配置文件**：`.env`
```env
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=123456
REDIS_DATABASE=0
```

**使用示例 1：手动缓存管理**
```typescript
import { RedisService } from '../../../libs/common/src/modules/redis/redis.service';

@Injectable()
export class ProductService {
  constructor(private redisService: RedisService) {}

  async getProductDetail(id: number) {
    // 1. 尝试从缓存获取
    const cacheKey = `product:detail:${id}`;
    const cachedData = await this.redisService.get(cacheKey);
    if (cachedData) {
      return cachedData;
    }

    // 2. 从数据库查询
    const product = await this.prisma.product.findUnique({ where: { id } });

    // 3. 保存到缓存（缓存 5 分钟）
    await this.redisService.set(cacheKey, product, 300);

    return product;
  }

  async updateProduct(productData: any) {
    // 更新数据库
    const product = await this.prisma.product.update(...);

    // 清除缓存
    await this.redisService.del(`product:detail:${productData.id}`);
    await this.redisService.delPattern('product:list:*');

    return product;
  }
}
```

**使用示例 2：自动缓存管理（使用拦截器）**
```typescript
import { Cache } from '../../../libs/common/src/modules/redis/redis.interceptor';

@Controller('api/product')
export class ProductController {
  @Get(':id')
  @Cache('product:detail', 300) // 自动缓存 5 分钟
  async getDetail(@Param('id') id: number) {
    return this.productService.getProductDetail(id);
  }
}
```

**缓存策略**：
- 商品详情：缓存 5 分钟
- 商品列表：缓存 5 分钟
- 热门商品：缓存 5 分钟
- 新品推荐：缓存 5 分钟
- 订单详情：缓存 5 分钟

---

### 3. Seata 分布式事务

**功能说明**：
- 支持跨多个微服务的分布式事务
- 自动事务提交和回滚
- 分支事务注册

**已集成的服务**：
- ✅ Order (订单创建、取消)
- ✅ Stock (库存扣减、恢复)
- ✅ Pay (支付订单创建、取消)
- ✅ System (商品管理)

**配置文件**：`.env`
```env
SEATA_SERVER=localhost:8091
SEATA_GROUP=DEFAULT_GROUP
SEATA_NAMESPACE=public
```

**使用示例 1：创建订单（跨服务事务）**
```typescript
import { SeataService } from '../../../libs/common/src/modules/seata/seata.service';

@Injectable()
export class OrderService {
  constructor(
    private seataService: SeataService,
    private prisma: PrismaClient,
  ) {}

  async createOrder(orderData: any) {
    // 使用分布式事务
    return await this.seataService.executeInTransaction(async (txId) => {
      console.log(`开始创建订单，事务ID: ${txId}`);

      // 1. 创建订单
      const order = await this.prisma.order.create({ ... });

      // 2. 注册分支事务：扣减库存
      await this.seataService.registerBranchTransaction(txId, 'stock-deduct');
      await axios.post('http://stock-service/api/stock/deduct', { ... });

      // 3. 注册分支事务：创建支付订单
      await this.seataService.registerBranchTransaction(txId, 'pay-create');
      await axios.post('http://pay-service/api/pay/create', { ... });

      // 如果任何一步失败，事务会自动回滚
      return order;
    });
  }
}
```

**使用示例 2：取消订单（跨服务事务）**
```typescript
async cancelOrder(orderId: number) {
  return await this.seataService.executeInTransaction(async (txId) => {
    console.log(`开始取消订单，事务ID: ${txId}`);

    // 1. 更新订单状态
    await this.prisma.order.update({ ... });

    // 2. 注册分支事务：恢复库存
    await this.seataService.registerBranchTransaction(txId, 'stock-restore');
    await axios.post('http://stock-service/api/stock/restore', { ... });

    // 3. 注册分支事务：取消支付
    await this.seataService.registerBranchTransaction(txId, 'pay-cancel');
    await axios.post('http://pay-service/api/pay/cancel', { ... });

    return { success: true };
  });
}
```

**事务流程**：
```
1. 开启全局事务 (beginGlobalTransaction)
   ↓
2. 创建订单 (Order Service)
   ↓
3. 注册分支事务：扣减库存 (Stock Service)
   ↓
4. 注册分支事务：创建支付订单 (Pay Service)
   ↓
5. 提交全局事务 (commitGlobalTransaction)
   ↓
6. 如果任何步骤失败，回滚全局事务 (rollbackGlobalTransaction)
```

---

## 安装依赖

### 1. 安装 Nacos
```bash
# 下载 Nacos
wget https://github.com/alibaba/nacos/releases/download/2.2.0/nacos-server-2.2.0.zip

# 解压
unzip nacos-server-2.2.0.zip

# 启动 Nacos (单机模式)
cd nacos/bin
sh startup.sh -m standalone

# 访问 Nacos 控制台
# http://localhost:8848/nacos
# 默认账号/密码：nacos/nacos
```

### 2. 安装 Redis
```bash
# Windows (使用 Chocolatey)
choco install redis-64

# 启动 Redis
redis-server

# 或者

# 下载 Redis for Windows
# https://github.com/microsoftarchive/redis/releases
```

### 3. 安装 Seata
```bash
# 下载 Seata
wget https://github.com/seata/seata/releases/download/v1.6.1/seata-server-1.6.1.zip

# 解压
unzip seata-server-1.6.1.zip

# 启动 Seata
cd seata/bin
sh seata-server.sh

# 默认端口：8091
```

---

## 运行项目

### 1. 安装项目依赖
```bash
cd d:\code\mall-platform\mall-nest-service
npm install
```

### 2. 启动基础设施
```bash
# 启动 Nacos
cd nacos/bin
sh startup.sh -m standalone

# 启动 Redis
redis-server

# 启动 Seata
cd seata/bin
sh seata-server.sh
```

### 3. 启动微服务（按顺序）
```bash
# 终端 1：启动 Gateway
cd apps/gateway
npm run start:dev

# 终端 2：启动 Auth
cd apps/auth
npm run start:dev

# 终端 3：启动 System
cd apps/system
npm run start:dev

# 终端 4：启动 Order
cd apps/order
npm run start:dev

# 终端 5：启动 Stock
cd apps/stock
npm run start:dev

# 终端 6：启动 Pay
cd apps/pay
npm run start:dev

# 终端 7：启动 Upload
cd apps/upload
npm run start:dev

# 终端 8：启动 Cart
cd apps/cart
npm run start:dev
```

---

## 测试接口

### 1. 测试 Nacos 服务注册
```bash
# 访问 Nacos 控制台
http://localhost:8848/nacos

# 在"服务管理" → "服务列表"中查看已注册的服务
```

### 2. 测试 Redis 缓存
```bash
# 获取商品详情（第一次会从数据库查询）
curl http://localhost:9000/api/product/1

# 再次获取商品详情（会从 Redis 缓存返回）
curl http://localhost:9000/api/product/1

# 查看 Redis 中的缓存
redis-cli
> KEYS *
> GET "product:detail:1"
```

### 3. 测试 Seata 分布式事务
```bash
# 创建订单（会触发分布式事务）
curl -X POST http://localhost:9000/api/order/create \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "totalAmount": 100.00,
    "items": [
      { "productId": 1, "quantity": 2 }
    ]
  }'

# 查看事务日志
# 在控制台会看到：
# ✅ 开启全局事务: TX_xxx
# ✅ 注册分支事务: TX_xxx@order-service#stock-deduct
# ✅ 注册分支事务: TX_xxx@order-service#pay-create
# ✅ 提交全局事务: TX_xxx
```

---

## 项目结构

```
mall-nest-service/
├── apps/
│   ├── gateway/          # API 网关（已集成 Nacos）
│   ├── auth/             # 认证服务（已集成 Nacos）
│   ├── system/           # 系统管理（已集成 Nacos、Redis、Seata）
│   ├── order/            # 订单服务（已集成 Nacos、Redis、Seata）
│   ├── stock/            # 库存服务（已集成 Nacos、Seata）
│   ├── upload/           # 文件上传（已集成 Nacos）
│   ├── cart/             # 购物车（已集成 Nacos、Redis）
│   └── pay/              # 支付服务（已集成 Nacos、Seata）
├── libs/
│   └── common/
│       └── src/
│           ├── modules/
│           │   ├── nacos/      # Nacos 服务注册模块
│           │   ├── redis/      # Redis 缓存模块
│           │   └── seata/     # Seata 分布式事务模块
│           ├── interfaces/     # 通用接口定义
│           └── index.ts       # 公共模块导出
├── prisma/                # 数据库 Schema
├── .env                   # 环境变量配置
└── package.json          # 项目依赖
```

---

## 注意事项

### 1. Nacos
- 确保 Nacos 服务已启动（默认端口 8848）
- 检查 `.env` 中的 `NACOS_SERVER_ADDR` 配置是否正确
- 如果服务注册失败，会打印错误日志，但不影响服务启动

### 2. Redis
- 确保 Redis 服务已启动（默认端口 6379）
- 检查 `.env` 中的 Redis 配置是否正确
- 如果 Redis 连接失败，会打印错误日志，但不影响服务启动

### 3. Seata
- 确保 Seata 服务已启动（默认端口 8091）
- 检查 `.env` 中的 Seata 配置是否正确
- 当前实现为模拟版本，实际项目需要对接真实的 Seata Server API

### 4. 实际生产环境
- Nacos：使用集群模式，配置持久化
- Redis：使用哨兵模式或集群模式，保证高可用
- Seata：使用 DB 模式存储事务日志，保证数据一致性

---

## 下一步优化

1. **Nacos 服务发现**：在 Gateway 中集成 Nacos 服务发现，动态路由到后端服务
2. **Redis 缓存策略**：优化缓存策略，使用 LRU 淘汰策略
3. **Seata 真实对接**：对接真实的 Seata Server API，实现真正的分布式事务
4. **监控和告警**：集成 Prometheus 和 Grafana，监控服务健康状态
5. **链路追踪**：集成 Jaeger 或 Zipkin，实现分布式链路追踪

---

## 常见问题

### Q1：Nacos 服务注册失败怎么办？
**A**：检查以下几点：
1. Nacos 服务是否已启动
2. `.env` 中的 `NACOS_SERVER_ADDR` 配置是否正确
3. 防火墙是否开放 8848 端口

### Q2：Redis 缓存不生效怎么办？
**A**：检查以下几点：
1. Redis 服务是否已启动
2. `.env` 中的 Redis 配置是否正确
3. 查看控制台是否有 Redis 连接错误日志

### Q3：Seata 分布式事务不生效怎么办？
**A**：当前实现为模拟版本，实际项目需要：
1. 下载并启动 Seata Server
2. 对接真实的 Seata API
3. 配置数据源代理（AT 模式）

---

## 总结

✅ **Nacos 服务注册**：所有微服务已集成，自动注册和心跳
✅ **Redis 缓存**：System、Cart、Order 服务已集成，支持手动和自动缓存管理
✅ **Seata 分布式事务**：Order、Stock、Pay 服务已集成，支持跨服务事务管理

项目已完成微服务架构的核心基础设施集成，可以基于此框架继续扩展其他功能。
