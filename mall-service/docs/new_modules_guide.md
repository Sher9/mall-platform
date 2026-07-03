# mall-service 新模块添加指南

## 新增模块说明

本次更新为 `mall-service` 添加了以下功能模块：

### 1. 文件上传模块 (upload)
- **端口**: 8003
- **服务名**: upload-service
- **功能**:
  - 单个文件上传：`POST /api/upload/single`
  - 多个文件上传：`POST /api/upload/multiple`
  - 文件删除：`DELETE /api/upload/{type}/{date}/{filename}`
- **文件存储目录**: `./uploads`
- **文件访问 URL**: `http://localhost:8000/files/**`

### 2. 购物车模块 (cart)
- **端口**: 8004
- **服务名**: cart-service
- **数据库**: mall_cart
- **功能**:
  - 添加商品到购物车：`POST /api/cart/add`
  - 获取购物车列表：`GET /api/cart/list`
  - 更新商品数量：`PUT /api/cart/quantity`
  - 删除购物车商品：`DELETE /api/cart/{cartItemId}`
  - 批量删除：`DELETE /api/cart/batch`
  - 更新选中状态：`PUT /api/cart/selected`
  - 全选/取消全选：`PUT /api/cart/selectAll`
  - 获取购物车数量：`GET /api/cart/count`

### 3. 支付模块 (pay)
- **端口**: 8005
- **服务名**: pay-service
- **数据库**: mall_pay
- **功能**:
  - 创建支付订单：`POST /api/pay/create`
  - 微信支付下单：`POST /api/pay/wechat`
  - 支付宝支付下单：`POST /api/pay/alipay`
  - 微信支付回调：`POST /api/pay/wechat/callback`
  - 支付宝支付回调：`POST /api/pay/alipay/callback`
  - 查询支付状态：`GET /api/pay/status`
  - 关闭支付订单：`POST /api/pay/close`
  - 退款：`POST /api/pay/refund`
- **注意**: 当前为模拟实现，实际对接需要配置微信/支付宝商户信息

### 4. 地址管理接口 (system 模块)
- **功能**:
  - 获取地址列表：`GET /api/address/list`
  - 获取地址详情：`GET /api/address/{id}`
  - 添加地址：`POST /api/address/add`
  - 更新地址：`PUT /api/address/update`
  - 删除地址：`DELETE /api/address/{id}`
  - 设置默认地址：`PUT /api/address/default/{id}`
  - 获取默认地址：`GET /api/address/default`

### 5. 首页接口 (system 模块)
- **功能**:
  - 获取首页数据：`GET /api/home/index`
  - 获取轮播图：`GET /api/home/banners`
  - 获取商品分类：`GET /api/home/categories`
  - 获取热门推荐：`GET /api/home/hot`
  - 获取新品推荐：`GET /api/home/new`

## 数据库初始化

执行以下 SQL 脚本创建相关表：

```bash
mysql -u root -p < sql/mall_new_tables.sql
```

## 文件上传目录

文件上传目录已创建：`./uploads`

文件访问方式：
- 上传的文件存储在 `./uploads/{type}/{date}/{filename}`
- 通过 `http://localhost:8000/files/{type}/{date}/{filename}` 访问

## 启动步骤

1. 启动 Nacos 服务注册中心
2. 启动 MySQL 数据库
3. 执行 SQL 脚本创建相关表
4. 启动各服务模块（按顺序）：
   - gateway (端口 8000)
   - system (端口 8001)
   - auth (端口 8002)
   - upload (端口 8003)
   - cart (端口 8004)
   - pay (端口 8005)
   - order (端口 8006)
   - stock (端口 8007)

## 测试接口

### 文件上传测试
```bash
curl -X POST http://localhost:8000/api/upload/single \
  -F "file=@/path/to/your/file.jpg" \
  -F "type=image"
```

### 购物车测试
```bash
curl -X POST "http://localhost:8000/api/cart/add?customerId=1&productId=1&quantity=2"
```

### 首页接口测试
```bash
curl http://localhost:8000/api/home/index
```

## 注意事项

1. 支付模块当前为模拟实现，实际对接需要：
   - 配置微信支付商户信息
   - 配置支付宝商户信息
   - 更新配置文件中的相关参数

2. 文件上传模块需要确保 `./uploads` 目录有写权限

3. 购物车模块需要创建 `mall_cart` 数据库

4. 支付模块需要创建 `mall_pay` 数据库

5. 地址管理和首页接口在 `system` 模块中，需要确保 `mall_system` 数据库中有相关表
