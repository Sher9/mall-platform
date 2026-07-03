# 商城在线购物平台 (mall-web)

基于 Nuxt 3 + Element Plus 构建的 SSR 在线商城项目

## 技术栈

- **框架**: Nuxt 3 (Vue 3 + TypeScript)
- **UI 组件**: Element Plus
- **状态管理**: Pinia
- **样式**: SCSS
- **HTTP 客户端**: $fetch (Nuxt 内置)

## 项目结构

```
mall-web/
├── assets/styles/      # 全局样式
├── components/         # 公共组件
├── composables/        # 组合式函数 (API 调用)
├── layouts/            # 页面布局
├── pages/              # 页面 (文件系统路由)
│   ├── index.vue      # 首页
│   ├── product/       # 商品相关页面
│   ├── cart.vue       # 购物车
│   ├── login.vue      # 登录
│   ├── register.vue   # 注册
│   └── user/         # 用户中心
├── plugins/           # 插件
├── store/             # Pinia 状态管理
├── types/             # TypeScript 类型定义
├── utils/             # 工具函数
├── server/api/        # 服务端 API 代理
├── public/            # 静态资源
├── nuxt.config.ts     # Nuxt 配置
└── package.json
```

## 页面功能

- **首页** (`/`): 轮播图、热门推荐、新品上市
- **商品列表** (`/product`): 商品搜索、分类筛选、排序
- **商品详情** (`/product/:id`): 商品信息、加入购物车、立即购买
- **购物车** (`/cart`): 商品管理、数量调整、批量删除、结算
- **登录** (`/login`): 手机号登录
- **注册** (`/register`): 用户注册
- **个人中心** (`/user/center`): 用户信息、订单状态
- **我的订单** (`/user/orders`): 订单列表、支付、确认收货

## 环境要求

- Node.js >= 18.0.0
- 后端服务 (mall-service) 运行在 `http://localhost:8000`

## 安装依赖

```bash
cd mall-web
npm install
```

如遇依赖冲突，使用:

```bash
npm install --legacy-peer-deps
```

## 启动开发服务器

```bash
npm run dev
```

项目将在 `http://localhost:3000` 启动

## 后端 API 说明

项目通过代理访问后端服务:

- `/api/product/**` -> 商品服务
- `/api/customer/**` -> 客户认证服务
- `/api/order/**` -> 订单服务
- `/api/stock/**` -> 库存服务

确保 `mall-service` 的各个服务已启动 (通过 Gateway 8000 端口访问)

## 构建生产版本

```bash
npm run build
npm run preview
```

## 注意事项

1. 需要先启动后端 `mall-service` 服务 (gateway 端口 8000)
2. 登录功能需要后端 `/api/customer/login` 接口正常
3. 购物车数据存储在浏览器 localStorage 中
4. 用户 token 存储在 localStorage 中
