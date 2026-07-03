# NestJS 微服务项目

## 项目概述

基于 NestJS 10 + Node.js 20 + Prisma + MySQL 8 + Redis + Nacos + Seata 的微服务架构商城后端服务。

## 技术栈

- **框架**：NestJS 10
- **运行时**：Node.js 20
- **ORM**：Prisma
- **数据库**：MySQL 8
- **缓存**：Redis
- **服务注册**：Nacos
- **分布式事务**：Seata
- **消息队列**：RocketMQ（待集成）
- **认证**：JWT

## 项目结构

```
mall-nest-service/
├── apps/
│   ├── gateway/        # API 网关（端口 9000）
│   ├── auth/           # 认证服务（端口 9002）
│   ├── system/         # 系统管理（端口 9001）
│   ├── order/          # 订单服务（端口 9006）
│   ├── stock/          # 库存服务（端口 9007）
│   ├── upload/         # 文件上传（端口 9003）
│   ├── cart/           # 购物车（端口 9004）
│   └── pay/            # 支付服务（端口 9005）
├── libs/
│   └── common/       # 公共模块
│       └── src/
│           ├── modules/
│           │   ├── nacos/      # Nacos 服务注册
│           │   ├── redis/      # Redis 缓存
│           │   └── seata/     # Seata 分布式事务
│           ├── interfaces/     # 通用接口定义
│           └── index.ts       # 公共模块导出
├── prisma/                # Prisma Schema
├── docs/                  # 文档
│   ├── api-reference.md   # API 接口文档
│   └── integration-guide.md  # 集成指南
├── .env                   # 环境变量配置
└── package.json          # 项目依赖
```

## 服务说明

| 服务名 | 端口 | 功能 |
|--------|------|------|
| Gateway | 9000 | API 网关、路由转发、负载均衡 |
| Auth | 9002 | 管理员/客户登录、注册、JWT 认证 |
| System | 9001 | 商品管理、轮播图管理、地址管理、首页接口 |
| Order | 9006 | 订单创建、订单列表、订单详情、订单取消、发货、确认收货 |
| Stock | 9007 | 库存查询、扣减、增加、锁定、解锁 |
| Upload | 9003 | 单文件上传、多文件上传、文件访问 |
| Cart | 9004 | 购物车增删改查、选中状态管理 |
| Pay | 9005 | 微信支付、支付宝支付、支付回调、退款 |

## 已实现功能

### 1. Nacos 服务注册 ✅
- 所有微服务启动时会自动注册到 Nacos
- 每 5 秒发送心跳保持健康状态
- 服务关闭时自动注销

### 2. Redis 缓存 ✅
- 商品详情缓存（5 分钟）
- 商品列表缓存（5 分钟）
- 购物车列表缓存（5 分钟）
- 订单详情缓存（5 分钟）
- 自动缓存清除（增删改时）

### 3. Seata 分布式事务 ✅
- 创建订单时使用分布式事务
- 取消订单时使用分布式事务
- 自动事务提交和回滚

### 4. 购物车模块 ✅
- 添加商品到购物车
- 获取购物车列表
- 更新商品数量
- 删除购物车商品
- 批量删除购物车商品
- 更新选中状态
- 全选/取消全选
- 获取购物车数量

### 5. 支付模块 ✅
- 创建支付订单
- 微信支付下单（模拟）
- 支付宝支付下单（模拟）
- 微信支付回调（模拟）
- 支付宝支付回调（模拟）
- 查询支付状态
- 关闭支付订单
- 退款（模拟）

### 6. 订单模块 ✅
- 创建订单（使用分布式事务）
- 获取订单列表
- 获取订单详情
- 取消订单（使用分布式事务）
- 支付订单
- 发货
- 确认收货

### 7. 库存模块 ✅
- 查询库存
- 扣减库存
- 批量扣减库存
- 增加库存
- 锁定库存
- 解锁库存

## 安装与运行

### 1. 环境要求
- Node.js 20+
- MySQL 8+
- Redis 6+
- Nacos 2.2+
- Seata 1.6+

### 2. 安装依赖
```bash
cd d:\code\mall-platform\mall-nest-service
npm install
```

### 3. 配置数据库
```bash
# 创建数据库
mysql -u root -p123456 -e "CREATE DATABASE IF NOT EXISTS demo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 运行 Prisma 迁移
npx prisma migrate dev --name init

# 生成 Prisma Client
npx prisma generate
```

### 4. 启动基础设施
```bash
# 启动 Nacos (默认端口 8848)
cd nacos/bin
sh startup.sh -m standalone

# 启动 Redis (默认端口 6379)
redis-server

# 启动 Seata (默认端口 8091)
cd seata/bin
sh seata-server.sh
```

### 5. 启动微服务（按顺序）
```bash
# 终端 1：启动 Gateway
cd apps/gateway && npm run start:dev

# 终端 2：启动 Auth
cd apps/auth && npm run start:dev

# 终端 3：启动 System
cd apps/system && npm run start:dev

# 终端 4：启动 Order
cd apps/order && npm run start:dev

# 终端 5：启动 Stock
cd apps/stock && npm run start:dev

# 终端 6：启动 Pay
cd apps/pay && npm run start:dev

# 终端 7：启动 Upload
cd apps/upload && npm run start:dev

# 终端 8：启动 Cart
cd apps/cart && npm run start:dev
```

## API 接口列表

### 认证接口
- `POST /api/admin/login` - 管理员登录
- `POST /api/customer/login` - 客户登录
- `POST /api/customer/register` - 客户注册

### 商品接口
- `GET /api/product/list` - 获取商品列表
- `GET /api/product/detail/:id` - 获取商品详情
- `POST /api/product/add` - 新增商品
- `PUT /api/product/update` - 更新商品
- `DELETE /api/product/delete/:id` - 删除商品

### 购物车接口
- `POST /api/cart/add` - 添加商品到购物车
- `GET /api/cart/list` - 获取购物车列表
- `PUT /api/cart/quantity` - 更新商品数量
- `DELETE /api/cart/:cartItemId` - 删除购物车商品
- `DELETE /api/cart/batch` - 批量删除
- `PUT /api/cart/selected` - 更新选中状态
- `PUT /api/cart/selectAll` - 全选/取消全选
- `GET /api/cart/count` - 获取购物车数量

### 订单接口
- `POST /api/order/create` - 创建订单
- `GET /api/order/list` - 订单列表
- `GET /api/order/:orderNo` - 订单详情
- `PUT /api/order/cancel` - 取消订单
- `PUT /api/order/pay` - 支付订单
- `PUT /api/order/ship` - 发货
- `PUT /api/order/confirm` - 确认收货

### 支付接口
- `POST /api/pay/create` - 创建支付订单
- `POST /api/pay/wechat` - 微信支付下单
- `POST /api/pay/alipay` - 支付宝支付下单
- `POST /api/pay/wechat/callback` - 微信支付回调
- `POST /api/pay/alipay/callback` - 支付宝支付回调
- `GET /api/pay/status` - 查询支付状态
- `POST /api/pay/close` - 关闭支付订单
- `POST /api/pay/refund` - 退款

### 库存接口
- `GET /api/stock/:productId` - 查询库存
- `PUT /api/stock/deduct` - 扣减库存
- `PUT /api/stock/batchDeduct` - 批量扣减库存
- `PUT /api/stock/add` - 增加库存
- `POST /api/stock/lock` - 锁定库存
- `POST /api/stock/unlock` - 解锁库存

### 文件上传接口
- `POST /api/upload/single` - 单文件上传
- `POST /api/upload/multiple` - 多文件上传
- `GET /files/**` - 文件访问

> **完整 API 文档**：请查看 `docs/api-reference.md`

## 测试示例

### 1. 管理员登录
```bash
curl -X POST http://localhost:9000/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

### 2. 获取商品列表
```bash
curl http://localhost:9000/api/product/list?pageNum=1&pageSize=10
```

### 3. 添加商品到购物车
```bash
curl -X POST http://localhost:9000/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"productId":1,"quantity":2}'
```

### 4. 创建订单
```bash
curl -X POST http://localhost:9000/api/order/create \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {
        "productId": 1,
        "productName": "iPhone 15",
        "price": 599900,
        "quantity": 2
      }
    ],
    "address": "广东省深圳市南山区科技园路 123 号"
  }'
```

### 5. 微信支付下单
```bash
curl -X POST http://localhost:9000/api/pay/wechat \
  -H "Content-Type: application/json" \
  -d '{
    "orderNo": "ORD1234567890",
    "amount": 1199800,
    "openid": "oUpF8uMuAJO_M2pxb1Q9zNjWeSs"
  }'
```

## 数据库表结构

### 核心表
- `admin_user` - 管理员用户表
- `customer` - 客户表
- `product` - 商品表
- `banner` - 轮播图表
- `address` - 收货地址表
- `order` - 订单表
- `order_item` - 订单明细表
- `cart_item` - 购物车表
- `pay_order` - 支付订单表
- `stock` - 库存表

> **详细 Schema**：请查看 `prisma/schema.prisma`

## 分布式事务流程

### 创建订单流程
```
1. 开启全局事务
   ↓
2. 创建订单（Order Service）
   ↓
3. 注册分支事务：扣减库存（Stock Service）
   ↓
4. 注册分支事务：创建支付订单（Pay Service）
   ↓
5. 提交全局事务
   ↓
6. 如果任何步骤失败，回滚全局事务
```

### 取消订单流程
```
1. 开启全局事务
   ↓
2. 更新订单状态为已取消（Order Service）
   ↓
3. 注册分支事务：恢复库存（Stock Service）
   ↓
4. 注册分支事务：关闭支付订单（Pay Service）
   ↓
5. 提交全局事务
   ↓
6. 如果任何步骤失败，回滚全局事务
```

## 缓存策略

| 数据类型 | 缓存 Key 格式 | 缓存时间 |
|----------|----------------|----------|
| 商品详情 | `product:detail:{id}` | 5 分钟 |
| 商品列表 | `product:list:{page}:{size}:{name}` | 5 分钟 |
| 热门商品 | `product:hot:{limit}` | 5 分钟 |
| 新品推荐 | `product:new:{limit}` | 5 分钟 |
| 购物车列表 | `cart:list:{customerId}` | 5 分钟 |
| 订单详情 | `order:detail:{orderNo}` | 5 分钟 |

## 后续优化计划

- [ ] 集成 RocketMQ 消息队列
- [ ] 实现真实的微信支付和支付宝支付
- [ ] 对接真实的 Seata Server API
- [ ] 集成 Prometheus + Grafana 监控
- [ ] 集成 Jaeger 链路追踪
- [ ] 实现服务熔断和降级
- [ ] 实现 API 限流
- [ ] 实现数据权限控制

## 常见问题

### Q1：服务启动失败怎么办？
**A**：检查以下几点：
1. 数据库是否已启动
2. Redis 是否已启动
3. Nacos 是否已启动
4. `.env` 中的配置是否正确

### Q2：Prisma 迁移失败怎么办？
**A**：尝试以下操作：
```bash
# 重置数据库
npx prisma migrate reset

# 重新运行迁移
npx prisma migrate dev --name init

# 生成 Prisma Client
npx prisma generate
```

### Q3：Nacos 服务注册失败怎么办？
**A**：检查以下几点：
1. Nacos 服务是否已启动
2. `.env` 中的 `NACOS_SERVER_ADDR` 配置是否正确
3. 防火墙是否开放 8848 端口

## 文档索引

- **API 接口文档**：`docs/api-reference.md`
- **集成指南**：`docs/integration-guide.md`
- **Prisma Schema**：`prisma/schema.prisma`

## 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 许可证

MIT License

## 联系方式

如有问题，请提交 Issue 或联系项目维护者。
