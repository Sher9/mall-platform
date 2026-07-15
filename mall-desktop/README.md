# mall-desktop

商城桌面端管理应用（Electron + Vue 3），作为 `mall-platform` 单体/微服务后台的前端客户端，提供商品、客户、订单等模块的可视化管理。

## 技术栈

- **Electron** 30 + **electron-vite** 构建
- **Vue 3** + **Vue Router 4** + **Pinia**
- **Axios** 请求封装 + 统一拦截
- **Sass（SCSS）** 企业级极简风样式

## 目录结构

```
mall-desktop/
├── electron.vite.config.js      # 主进程 / 预加载 / 渲染进程构建配置
├── package.json
├── .npmrc                       # Electron 二进制国内镜像（ELECTRON_MIRROR）
└── src/
    ├── main/                    # Electron 主进程
    ├── preload/                 # 预加载脚本
    └── renderer/                # 渲染进程（Vue 应用）
        ├── index.html
        ├── main.js
        ├── App.vue
        ├── assets/styles/       # 全局样式（variables.scss / global.scss）
        ├── api/                 # 接口封装（request / auth / product / customer / order）
        ├── components/          # 公共组件（Layout 布局）
        ├── router/              # 路由及登录守卫
        ├── stores/              # Pinia 状态（user）
        └── views/               # 页面（登录、首页、商品、客户、订单、购物车等）
```

## 环境要求

- Node.js ≥ 18
- 后台服务 `mall-service` 已启动（网关监听 **8000** 端口，Nacos 8848、Redis 6379、MySQL 3306 就绪）

## 安装与运行

```bash
# 安装依赖（.npmrc 已配置 Electron 国内镜像，避免从 GitHub 下载失败）
npm install

# 启动开发模式（同时拉起 Vite dev server 与 Electron 窗口）
npm run dev
```

> ⚠️ 若 `npm run dev` 报 `Error: Electron uninstall`，说明 `node_modules/electron/dist` 二进制未下载成功。
> 可执行 `node node_modules/electron/install.js` 手动触发下载，或重新 `npm install`。

## 构建与打包

```bash
# 仅构建（输出到 out/）
npm run build

# Windows 安装包
npm run build:win

# macOS 安装包
npm run build:mac
```

## 后端接口配置

前端通过网关访问后台，默认地址在 `src/renderer/src/api/request.js`：

```js
const request = axios.create({
  baseURL: 'http://localhost:8000/api',  // 网关端口 8000，非 8080
  timeout: 10000
})
```

网关路由（`mall-service/gateway`）将以下前缀转发到对应微服务（StripPrefix=1）：

| 前端路径前缀 | 转发服务 |
| --- | --- |
| `/api/admin/**`、`/api/customer/**` | cloud-auth |
| `/api/product/**`、`/api/customers/**`、`/api/home/**` 等 | cloud-system |
| `/api/order/**` | cloud-order |

## 登录说明

- 应用启动默认进入**登录页**，未登录访问任何页面会被路由守卫强制跳转回登录页；登录态失效（HTTP 401）也会自动跳转登录页。
- 默认管理员账号（来自 `mall-service` 的 auth 模块）：
  - 用户名：`admin`
  - 密码：`123456`
- 登录成功后接口返回 `accessToken`，前端存入 `localStorage` 并在后续请求以 `Authorization: Bearer <token>` 携带。
- 登录 / 登出入口位于顶部栏：未登录显示「登录」按钮，已登录显示头像、用户名与「退出」。

## 已实现页面

- 登录页（`/login`）
- 首页（`/home`）：数据概览与快捷入口
- 商品管理（`/products`、`/products/:id` 详情）
- 客户管理（`/customers`）
- 订单管理（`/orders`）
- 购物车（`/cart`）

## 常见问题

1. **请求报 `Network Error`**：确认后台 `mall-service` 网关已在 `8000` 端口监听，且 `baseURL` 为 `http://localhost:8000/api`（不是 8080）。
2. **登录后带不上鉴权 / 401**：确认登录响应字段为 `accessToken`，由 `stores/user.js` 正确读取并存入 `localStorage`。
3. **Electron 启动失败 `Electron uninstall`**：见上方「安装与运行」提示，确保 `node_modules/electron/dist` 存在。
4. **调试 ctr+shift+i**
