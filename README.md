# Mall Platform - 商城平台

企业级全栈电商解决方案，涵盖多端应用与微服务架构后端。

## 项目结构

```
mall-platform/
├── mall-admin/          # 管理后台前端 (Vue 3 + Element Plus)
├── mall-web/            # 商城 Web 端 (Nuxt 3 + SSR)
├── mall-miniapp/        # 微信小程序 (uni-app + Vue 3)
├── mall-desktop/        # 桌面端应用 (Electron + Vue 3)
├── mall-android/        # Android 原生应用 (Kotlin)
├── mall-service/        # 微服务后端 (Spring Boot 3 + Spring Cloud)
└── mall-nest-service/   # NestJS 微服务后端 (NestJS 10 + Prisma)
```

---

## 项目详情

### 1. mall-admin - 管理后台前端

**简介**：商城管理后台，提供商品管理、订单管理、用户管理、数据看板等功能。

**技术栈**：
- **框架**：Vue 3.4 + TypeScript
- **构建工具**：Vite 5
- **UI 组件库**：Element Plus 2.7
- **状态管理**：Pinia + pinia-plugin-persistedstate
- **路由**：Vue Router 4
- **HTTP 客户端**：Axios
- **图表**：ECharts 5 + echarts-wordcloud
- **日期处理**：Day.js
- **开发工具**：ESLint + TypeScript ESLint

**启动方式**：
```bash
cd mall-admin
npm install
npm run dev
```

---

### 2. mall-web - 商城 Web 端

**简介**：面向消费者的商城 Web 应用，支持 SSR 渲染，提供良好的 SEO 和用户体验。

**技术栈**：
- **框架**：Nuxt 3.17 (基于 Vue 3.5)
- **UI 组件库**：Element Plus
- **状态管理**：Pinia 3
- **样式**：TailwindCSS + Sass
- **工具库**：VueUse
- **HTTP 客户端**：Axios (通过 Nuxt 代理)
- **开发工具**：TypeScript + Node.js Types

**启动方式**：
```bash
cd mall-web
npm install
npm run dev
```

---

### 3. mall-miniapp - 微信小程序

**简介**：基于 uni-app 的跨端小程序，支持编译到微信小程序和 H5。

**技术栈**：
- **框架**：uni-app 3.0 (Vue 3)
- **编译工具**：Vite + @dcloudio/vite-plugin-uni
- **状态管理**：Pinia
- **国际化**：vue-i18n
- **UI 组件库**：uni-ui
- **日期处理**：Day.js
- **样式**：Sass Embedded

**启动方式**：
```bash
# 微信小程序
cd mall-miniapp
npm install
npm run dev:mp-weixin

# H5
npm run dev:h5
```

---

### 4. mall-desktop - 桌面端应用

**简介**：基于 Electron 的跨平台桌面应用，支持 Windows 和 macOS。

**技术栈**：
- **框架**：Electron 30 + Vue 3.4
- **构建工具**：electron-vite 2
- **状态管理**：Pinia
- **路由**：Vue Router 4
- **HTTP 客户端**：Axios
- **打包工具**：electron-builder
- **样式**：Sass

**启动方式**：
```bash
cd mall-desktop
npm install
npm run dev
```

**打包**：
```bash
# Windows
npm run build:win

# macOS
npm run build:mac
```

---

### 5. mall-android - Android 原生应用

**简介**：基于原生 Android 的模块化商城应用，采用 Kotlin 开发。

**技术栈**：
- **语言**：Kotlin 1.9
- **最低支持**：Android SDK 24 (Android 7.0)
- **目标版本**：Android SDK 34 (Android 14)
- **依赖注入**：Hilt (Dagger) 2.48
- **路由框架**：ARouter 1.5
- **视图绑定**：ViewBinding + DataBinding
- **构建工具**：Gradle 8.2
- **JDK 版本**：17

**模块结构**：
```
app/           # 主应用壳
base/          # 基础库模块
module_user/   # 用户模块
module_home/   # 首页模块
module_product/# 商品模块
module_cart/   # 购物车模块
module_order/  # 订单模块
module_pay/    # 支付模块
```

**启动方式**：
1. 使用 Android Studio 打开 `mall-android` 目录
2. 同步 Gradle 后运行

---

### 6. mall-service - 微服务后端 (Spring Cloud)

**简介**：基于 Spring Boot 3 和 Spring Cloud Alibaba 的企业级微服务架构。

**技术栈**：
- **框架**：Spring Boot 3.2.5
- **微服务套件**：Spring Cloud 2023.0.2
- **阿里云微服务**：Spring Cloud Alibaba 2023.0.1.2
- **服务注册/配置中心**：Nacos 2.3.2
- **熔断限流**：Sentinel 1.8.7
- **分布式事务**：Seata 2.0.0
- **ORM**：MyBatis-Plus 3.5.10
- **数据库**：MySQL 8.3
- **缓存**：Redis 7.0
- **消息队列**：RocketMQ 4.9.8 (Spring Boot Starter 2.2.3)
- **认证**：JWT (jjwt 0.12.3)
- **连接池**：Druid 1.2.20
- **Excel 处理**：Apache POI 5.2.5
- **简化代码**：Lombok 1.18.34
- **JDK 版本**：17

**服务模块**：
```
common/    # 公共模块
gateway/   # 网关服务
auth/      # 认证服务
system/    # 系统管理服务
order/     # 订单服务
stock/     # 库存服务
upload/    # 文件上传服务
cart/      # 购物车服务
pay/       # 支付服务
```

**启动方式**：
```bash
cd mall-service
mvn clean install
# 依次启动各服务模块
```

---

### 7. mall-nest-service - NestJS 微服务后端

**简介**：基于 NestJS 的现代微服务架构，使用 Prisma ORM。

**技术栈**：
- **框架**：NestJS 10
- **语言**：TypeScript
- **ORM**：Prisma 5
- **缓存**：Redis (ioredis 5)
- **服务注册/配置中心**：Nacos 2.2
- **消息队列**：RocketMQ
- **认证**：Passport + JWT (@nestjs/jwt)
- **密码加密**：bcrypt 5
- **验证**：class-validator + class-transformer
- **HTTP 客户端**：Axios

**微服务模块**：
```
apps/
├── gateway/   # API 网关
├── auth/      # 认证服务
├── system/    # 系统服务
├── order/     # 订单服务
├── stock/     # 库存服务
├── upload/    # 上传服务
├── cart/      # 购物车服务
└── pay/       # 支付服务
```

**启动方式**：
```bash
cd mall-nest-service
npm run install:all
# 分别启动各微服务
npm run dev:gateway
npm run dev:auth
# ...
```

---

## 技术架构概览

| 端/层 | 技术栈 |
|-------|--------|
| **管理后台** | Vue 3 + Element Plus + Vite |
| **Web 商城** | Nuxt 3 + Element Plus + SSR |
| **微信小程序** | uni-app + Vue 3 |
| **桌面端** | Electron + Vue 3 |
| **Android 端** | Kotlin + Hilt + ARouter |
| **Java 微服务** | Spring Boot 3 + Spring Cloud Alibaba |
| **Node 微服务** | NestJS 10 + Prisma + TypeScript |

---

## 开发环境要求

- **JDK**：17+
- **Node.js**：18+
- **Android Studio**：Hedgehog | 2023.1.1+
- **MySQL**：8.0+
- **Redis**：7.0+
- **Nacos**：2.3+
- **RocketMQ**：4.9+

---

## 许可证

MIT License
