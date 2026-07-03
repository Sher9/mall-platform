# 商城 Android App (mall-android)

基于 **组件化 + Clean MVVM + Jetpack 全套** 的 Android 原生商城应用

## 技术架构

### 1. 工程架构 - 组件化模块化

```
mall-android/
├── app/                    # 主 App 壳工程（只负责路由与初始化）
├── base/                   # 基础层（网络、工具、权限、公共控件）
├── module_user/            # 用户模块（登录、会员、收货地址）
├── module_home/            # 首页模块（首页、秒杀活动、商品分类）
├── module_product/         # 商品模块（商品列表、SKU 详情）
├── module_cart/            # 购物车模块（购物车本地 + 网络数据管理）
├── module_order/           # 订单模块（下单、支付、售后订单）
└── module_pay/            # 支付模块（微信/支付宝支付 SDK）
```

**路由框架**: **ARouter**（国内安卓商城唯一首选）

### 2. 代码分层 - Clean 整洁三层

#### ① Presentation 表现层（UI 层）
- **View**（Activity/Fragment/Compose 页面）
- **ViewModel**（Jetpack ViewModel）
- **StateFlow/SharedFlow**（响应式数据）
- **MVVM 模式**，View 不持有业务逻辑，纯数据驱动页面刷新，无内存泄漏

#### ② Domain 领域层（业务层，商城核心）
- **UseCase**（用例）
- 把下单、库存校验、价格计算、优惠券抵扣等复杂业务抽离出来，做到可单元测试
- 这一层**不依赖任何 Android 框架代码**，纯 Kotlin 逻辑，可被多个页面复用

#### ③ Data 数据层
- **Remote**：Retrofit 网络请求（后端商城接口）
- **Local**：Room 本地数据库（购物车离线缓存、浏览记录）
- **Repository** 屏蔽网络与本地差异，页面只关心拿数据，不用区分在线/离线

**数据流走向**：
```
View → ViewModel → UseCase → Repository → 网络/本地数据库
```

### 3. 设计模式

- **MVVM**（中小商城首选）

### 4. 语言与 UI

- **语言**: **Kotlin**（官方首选）
- **UI 方案**:
  - 稳定版: **XML + ViewBinding**
  - 新版: **Jetpack Compose**（商品页面开发效率极高）

### 5. 完整技术栈

#### Jetpack 核心组件（MVVM 必备）
- **ViewModel**: 生命周期托管
- **Lifecycle**: 生命周期监听
- **StateFlow + Coroutine 协程**: 异步请求，替代老旧 LiveData
- **Room**: 本地数据库（购物车缓存）
- **WorkManager**: 订单后台轮询、消息同步

#### 网络与存储（商城标配）
- **网络**: **Retrofit + OkHttp**（统一封装请求、token 自动续期、弱网重试）
- **图片**: **Glide**（商品大图、列表图片优化）
- **本地持久化**: **Room + MMKV**

#### 依赖注入
- **Hilt**（谷歌官方 DI，替代 Dagger，简化 ViewModel 与仓库注入）

#### 第三方 SDK
- **ARouter**: 组件跳转
- **微信/支付宝支付 SDK**: 支付功能
- **推送**: 极光推送
- **日志**: Bugly 崩溃收集

## 项目结构详解

### base 基础层

```
base/
├── src/main/java/com/example/mall/base/
│   ├── network/           # 网络层
│   │   ├── RetrofitManager.kt    # Retrofit + OkHttp 封装
│   │   └── ApiService.kt         # 后端 API 接口定义
│   ├── data/              # 数据层
│   │   ├── model/         # 数据模型（Product、Order、User等）
│   │   ├── repository/    # 数据仓库（屏蔽网络与本地差异）
│   │   ├── local/        # 本地数据库（Room）
│   │   └── remote/       # 远程数据源
│   ├── di/               # 依赖注入模块（Hilt）
│   ├── router/           # ARouter 路由配置
│   ├── utils/            # 工具类
│   └── usecase/          # 业务用例（Domain 层）
```

### 业务模块

每个业务模块都遵循 **Clean MVVM** 架构：

```
module_xxx/
├── src/main/java/com/example/mall/xxx/
│   ├── ui/               # 页面（Activity/Fragment/Compose）
│   ├── viewmodel/        # ViewModel
│   ├── usecase/          # 业务用例
│   ├── repository/       # 数据仓库
│   └── di/              # 依赖注入
├── src/main/res/         # 资源文件
└── build.gradle          # 模块配置
```

## 环境要求

- **Android Studio**: Hedgehog | 2023.1.1 或更高版本
- **JDK**: 17
- **Gradle**: 8.2
- **minSdk**: 24 (Android 7.0)
- **targetSdk**: 34 (Android 14)
- **Kotlin**: 1.9.20
- **Compose**: 2024.02.02

## 安装与运行

### 1. 克隆项目

```bash
git clone <repository-url>
cd mall-android
```

### 2. 配置后端服务地址

在 `base/src/main/java/com/example/mall/base/network/RetrofitManager.kt` 中修改：

```kotlin
private const val BASE_URL = "http://your-server-ip:8000/api/"
```

确保 `mall-service` 后端服务已启动（Gateway 端口 8000）

### 3. 配置微信/支付宝支付

在 `module_pay/src/main/java/com/example/mall/pay/PayManager.kt` 中修改：

```kotlin
private const val WECHAT_APP_ID = "your_wechat_app_id"
private const val ALIPAY_APP_ID = "your_alipay_app_id"
```

### 4. 同步项目

点击 Android Studio 中的 **Sync Now** 按钮，等待 Gradle 同步完成

### 5. 运行项目

- 连接 Android 设备或启动模拟器
- 点击 **Run 'app'** 按钮

## 功能清单

### 已实现功能

- [x] 组件化架构搭建
- [x] Clean MVVM 分层
- [x] Hilt 依赖注入
- [x] ARouter 路由配置
- [x] Retrofit 网络封装
- [x] 登录页面 (XML + ViewBinding)
- [x] 首页 (Jetpack Compose)
- [x] 商品详情页 (Jetpack Compose)
- [x] 购物车页面 (XML + RecyclerView)
- [x] 订单列表页面 (XML + RecyclerView)
- [x] 支付管理器 (微信/支付宝 SDK 封装)

### 待实现功能

- [ ] 注册页面
- [ ] 个人中心页面
- [ ] 商品分类页面
- [ ] 搜索页面
- [ ] 确认订单页面
- [ ] 订单详情页面
- [ ] 收货地址管理
- [ ] 秒杀活动页面
- [ ] 微信支付 SDK 集成
- [ ] 支付宝支付 SDK 集成
- [ ] 极光推送集成
- [ ] Bugly 崩溃收集集成

## 页面路由表

| 页面 | 路由路径 | 实现方式 |
|------|----------|------------|
| 登录 | `/user/login` | XML + ViewBinding |
| 注册 | `/user/register` | XML + ViewBinding |
| 首页 | `/home/main` | Jetpack Compose |
| 商品列表 | `/product/list` | Jetpack Compose |
| 商品详情 | `/product/detail` | Jetpack Compose |
| 购物车 | `/cart/main` | XML + RecyclerView |
| 订单列表 | `/order/list` | XML + RecyclerView |
| 订单详情 | `/order/detail` | Jetpack Compose |
| 支付 | `/pay/main` | 原生 SDK |

## 开发规范

### 命名规范

- **包名**: 全小写，多级包名用 `.` 分隔
- **类名**: 大驼峰命名法（如 `ProductRepository`）
- **函数名**: 小驼峰命名法（如 `getProductList`）
- **常量**: 全大写 + 下划线（如 `BASE_URL`）
- **变量**: 小驼峰命名法（如 `productList`）

### 代码分层规范

- **Presentation 层**: 只负责 UI 渲染和用户交互，不持有业务逻辑
- **Domain 层**: 纯 Kotlin 业务逻辑，不依赖 Android 框架
- **Data 层**: 负责数据获取和存储，对外提供统一的数据访问接口

### 协程使用规范

- 在 ViewModel 中使用 `viewModelScope.launch`
- 在 Repository 中使用 `suspend` 关键字
- 使用 `StateFlow` 替代 LiveData
- 使用 `catch` 操作符处理异常

## 第三方 SDK 集成指南

### 微信支付

1. 前往微信开放平台注册应用
2. 下载微信支付 SDK
3. 将 SDK jar 包放入 `module_pay/libs/` 目录
4. 在 `build.gradle` 中添加依赖
5. 配置微信支付参数

### 支付宝支付

1. 前往支付宝开放平台注册应用
2. 下载支付宝支付 SDK
3. 将 SDK jar 包放入 `module_pay/libs/` 目录
4. 在 `build.gradle` 中添加依赖
5. 配置支付宝支付参数

### 极光推送

1. 前往极光推送官网注册应用
2. 在 `build.gradle` 中添加依赖
3. 配置 AppKey 和 AppSecret
4. 初始化推送服务

## 打包发布

### 生成签名密钥

```bash
keytool -genkey -v -keystore mall-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias mall
```

### 配置签名

在 `app/build.gradle` 中添加：

```gradle
android {
    signingConfigs {
        release {
            storeFile file('mall-release-key.jks')
            storePassword 'your_store_password'
            keyAlias 'mall'
            keyPassword 'your_key_password'
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### 打包 APK

```bash
./gradlew assembleRelease
```

## 常见问题

### 1. ARouter 路由找不到路径

**解决方案**: 确保所有模块都正确配置了 ARouter 注解处理器，并且在使用路由的 Activity/Fragment 上添加了 `@Route` 注解

### 2. Hilt 依赖注入失败

**解决方案**: 确保 Application 类添加了 `@HiltAndroidApp` 注解，并且所有使用依赖注入的 Activity/Fragment 都添加了 `@AndroidEntryPoint` 注解

### 3. Compose 页面不显示

**解决方案**: 确保在 `build.gradle` 中启用了 Compose 支持，并且添加了正确的 Compose 依赖

### 4. 网络连接失败

**解决方案**: 确保在 AndroidManifest.xml 中添加了网络权限，并且后端服务已启动

## 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/xxx`)
3. 提交更改 (`git commit -m 'Add xxx feature'`)
4. 推送到分支 (`git push origin feature/xxx`)
5. 创建 Pull Request

## 开源协议

本项目采用 MIT 协议开源

## 联系我们

如有问题，请提交 Issue 或联系开发团队。

---

**注意**: 本项目为商城演示项目，实际商用需要替换后端服务地址、配置支付参数、优化性能等。
