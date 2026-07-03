# 企业级微服务管理后台

基于 Spring Boot 3.2 + Spring Cloud 2023.0.x + Spring Cloud Alibaba 构建的企业级微服务管理后台系统。

## 微服务架构

```
┌─────────────────────────────────────────────────────────┐
│                   API Gateway :8080                     │
│              Spring Cloud Gateway + Sentinel            │
└─────┬──────────────┬──────────────┬─────────────────────┘
      │              │              │
      ▼              ▼              ▼
┌──────────┬──────────────┬──────────┬──────────┐
│  auth   │    system    │  order   │  stock   │
│  :8081  │    :8082     │  :8085   │  :8086   │
│  认证   │ 用户/角色/   │@GlobalTx │          │
│  服务   │ 菜单/客户/   │  订单    │  库存    │
│         │   产品       │  管理    │  管理    │
└──────────┴──────────────┴──────────┴──────────┘
                         │          │
                  Feign远程调用  RocketMQ消息
                  Seata分布式事务

┌─────────────────────────────────────────────────────────┐
│       Nacos :8848      Redis :6379                      │
│       注册/配置中心        缓存                           │
├─────────────────────────────────────────────────────────┤
│    RocketMQ :9876       Seata :8091                     │
│      消息队列             分布式事务                      │
└─────────────────────────────────────────────────────────┘
```

## 技术栈

| 组件 | 版本 | 说明 |
| :--- | :--- | :--- |
| JDK | 17 | Java开发环境 |
| Spring Boot | 3.2.5 | 应用框架 |
| Spring Cloud | 2023.0.2 | 微服务框架 |
| Spring Cloud Alibaba | 2023.0.1.2 | 阿里微服务组件 |
| Nacos | 2.3.2 | 注册/配置中心 |
| Spring Cloud Gateway | 4.x | API网关 |
| OpenFeign | 4.x | RPC调用 |
| Spring Cloud LoadBalancer | 内置 | 负载均衡 |
| Sentinel | 1.8.7 | 熔断限流 |
| Seata | 2.0.0 | 分布式事务 |
| RocketMQ | 4.x | 消息队列 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| MySQL | 8.3.0 | 数据库 |
| Redis | 7.0.12 | 缓存 |
| JWT | 0.12.3 | 身份认证 |
| Druid | 1.2.20 | 数据库连接池 |

## 项目结构

```
mall-service/
├── pom.xml                    # 父POM
├── sql/init.sql               # 数据库初始化脚本
├── common/                    # 公共模块（Result/JWT/异常/Feign）
├── gateway/                   # API网关 :8080
├── auth/                      # 认证服务 :8081
├── system/                    # 系统管理服务 :8082
│   ├── entity/                # User/Role/Menu/Customer/Product
│   ├── mapper/                # 数据访问层
│   ├── service/impl/          # 业务逻辑+RocketMQ消息发送
│   └── controller/            # 所有业务Controller
├── order/                     # 订单管理服务 :8085
│   ├── feign/                 # Feign调用stock-service
│   └── @GlobalTransactional   # Seata分布式事务
└── stock/                     # 库存管理服务 :8086
    └── mq/                    # RocketMQ消费者
```

## 服务职责

| 服务 | 端口 | 数据表 | 核心功能 |
| :--- | :--- | :--- | :--- |
| gateway | 8080 | - | 统一入口、路由转发、JWT认证、Sentinel限流 |
| auth | 8081 | sys_user | 登录/注册/退出、JWT签发、Token管理 |
| system | 8082 | sys_role, sys_menu, sys_user_role, sys_role_menu, customer, product | 用户/角色/菜单/权限管理、客户CRUD、产品CRUD |
| order | 8085 | order_info | 订单创建/支付/发货/收货/取消 (Seata分布式事务) |
| stock | 8086 | stock_info, stock_log | 库存管理、入库/出库/锁定/解锁、RocketMQ消费 |

## 微服务间调用

```
order创建订单 → Feign → stock锁定库存   (Seata全局事务)
order支付     → RocketMQ → stock消费消息
order发货     → Feign → stock扣减库存   (Seata全局事务)
order取消     → Feign → stock解锁库存   (Seata全局事务)
system客户变更 → RocketMQ → customer-topic
system产品变更 → RocketMQ → product-topic
stock库存变更  → RocketMQ → stock-topic
```

## 快速开始

### 中间件启动

| 中间件 | 端口 | 命令 |
| :--- | :--- | :--- |
| MySQL 8.0+ | 3306 | 创建 example_db，执行 sql/init.sql |
| Redis 7.0+ | 6379 | redis-server |
| Nacos 2.3+ | 8848 | startup.cmd -m standalone |
| Seata 2.0+ | 8091 | seata-server.sh -p 8091 |
| RocketMQ 4.9+ | 9876 | mqnamesrv & mqbroker -n localhost:9876 |

### 编译启动

```bash
mvn clean package -DskipTests

# 启动顺序
java -jar gateway/target/gateway-1.0.0.jar
java -jar auth/target/auth-1.0.0.jar
java -jar system/target/system-1.0.0.jar
java -jar order/target/order-1.0.0.jar
java -jar stock/target/stock-1.0.0.jar
```

## 接口文档

所有接口通过网关统一访问：`http://localhost:8080/api/system/xxx`

### 认证接口 (auth-service)

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| POST | `/api/auth/login` | 用户名密码登录 |
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/logout?username=xxx` | 退出 |

### 系统管理 (system-service)

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| GET | `/api/system/roles` | 角色分页列表 |
| POST | `/api/system/roles` | 创建角色 |
| PUT | `/api/system/roles` | 更新角色 |
| DELETE | `/api/system/roles/{roleId}` | 删除角色 |
| GET | `/api/system/menus` | 菜单分页列表 |
| POST | `/api/system/menus` | 创建菜单 |
| PUT | `/api/system/menus` | 更新菜单 |
| DELETE | `/api/system/menus/{menuId}` | 删除菜单 |
| GET | `/api/system/menus/role/{roleId}` | 获取角色菜单 |

### 客户管理 (system-service)

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| GET | `/api/system/customers?pageNum=1&pageSize=10` | 客户分页列表 |
| GET | `/api/system/customers/{customerId}` | 客户详情 |
| POST | `/api/system/customers` | 创建客户 |
| PUT | `/api/system/customers` | 更新客户 |
| DELETE | `/api/system/customers/{customerId}` | 删除客户 |

### 产品管理 (system-service)

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| GET | `/api/system/products?pageNum=1&pageSize=10` | 产品分页列表 |
| GET | `/api/system/products/{productId}` | 产品详情 |
| POST | `/api/system/products` | 创建产品 |
| PUT | `/api/system/products` | 更新产品 |
| DELETE | `/api/system/products/{productId}` | 删除产品 |

### 订单管理 (order-service)

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| GET | `/api/order?pageNum=1&pageSize=10` | 订单分页列表 |
| GET | `/api/order/{orderId}` | 订单详情 |
| POST | `/api/order` | 创建订单 (Seata分布式事务) |
| PUT | `/api/order/{orderId}/pay` | 支付 |
| PUT | `/api/order/{orderId}/ship` | 发货 (Seata) |
| PUT | `/api/order/{orderId}/receive` | 确认收货 |
| PUT | `/api/order/{orderId}/cancel` | 取消 (Seata) |

### 库存管理 (stock-service)

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| GET | `/api/stock?pageNum=1&pageSize=10` | 库存分页列表 |
| GET | `/api/stock/{stockId}` | 库存详情 |
| GET | `/api/stock/product/{productId}` | 按产品查库存 |
| POST | `/api/stock` | 创建库存 |
| POST | `/api/stock/increase` | 入库操作 |
| GET | `/api/stock/logs?productId=1` | 库存变动日志 |

## Seata分布式事务

配置位置：[order/application.yml](file:///d:/project/java/SpringBoot3/order/src/main/resources/application.yml) 和 [stock/application.yml](file:///d:/project/java/SpringBoot3/stock/src/main/resources/application.yml)

```java
// OrderServiceImpl.java - 订单创建时全局事务
@GlobalTransactional(rollbackFor = Exception.class)
public OrderInfo createOrder(OrderInfo order) {
    orderInfoMapper.insert(order);           // 本地操作
    stockFeignClient.increaseStock(params);  // Feign远程→stock锁定库存
    // 任一环节异常→全局回滚
}
```

Nacos需配置 (DataId: `seataServer.properties`, Group: `SEATA_GROUP`)：
```
service.vgroupMapping.order-service-tx-group=default
service.vgroupMapping.stock-service-tx-group=default
```

## RocketMQ消息队列

| Topic | 生产者 | 消费者 | 
| :--- | :--- | :--- | 
| `order-topic` | order-service | [StockConsumer.java](file:///d:/project/java/SpringBoot3/stock/src/main/java/com/example/stock/mq/StockConsumer.java) |
| `customer-topic` | system-service | - |
| `product-topic` | system-service | - |
| `stock-topic` | stock-service | - |

## 数据库表

| 表名 | 所属服务 | 说明 |
| :--- | :--- | :--- |
| sys_user | auth | 系统用户 |
| sys_role | system | 角色 |
| sys_menu | system | 菜单 |
| sys_user_role | system | 用户角色关联 |
| sys_role_menu | system | 角色菜单关联 |
| customer | system | 客户 |
| product | system | 产品 |
| order_info | order | 订单 |
| stock_info | stock | 库存 |
| stock_log | stock | 库存变动日志 |
