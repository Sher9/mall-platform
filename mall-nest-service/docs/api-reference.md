# NestJS 微服务项目 - API 接口文档

## 基础信息

- **基础路径**：`http://localhost:9000`（通过 Gateway 访问）
- **直接访问**：`http://localhost:900X`（直接访问各服务）
- **认证方式**：Bearer Token（JWT）

---

## 1. 认证接口 (Auth Service - 端口 9002)

### 1.1 管理员登录
- **接口**：`POST /api/admin/login`
- **描述**：管理员登录，获取 JWT Token
- **请求体**：
  ```json
  {
    "username": "admin",
    "password": "123456"
  }
  ```
- **响应**：
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "tokenType": "Bearer",
      "userId": 1,
      "username": "admin",
      "nickname": "管理员"
    }
  }
  ```

### 1.2 客户登录
- **接口**：`POST /api/customer/login`
- **描述**：客户登录，获取 JWT Token
- **请求体**：
  ```json
  {
    "username": "test",
    "password": "123456"
  }
  ```

### 1.3 客户注册
- **接口**：`POST /api/customer/register`
- **描述**：客户注册
- **请求体**：
  ```json
  {
    "username": "test",
    "password": "123456",
    "nickname": "测试用户",
    "phone": "13800138000"
  }
  ```

---

## 2. 商品接口 (System Service - 端口 9001)

### 2.1 获取商品列表
- **接口**：`GET /api/product/list`
- **描述**：分页获取商品列表
- **参数**：
  - `pageNum`：页码（默认 1）
  - `pageSize`：每页数量（默认 10）
  - `productName`：商品名称（模糊搜索，可选）
- **示例**：`GET /api/product/list?pageNum=1&pageSize=10&productName=手机`

### 2.2 获取商品详情
- **接口**：`GET /api/product/detail/:id`
- **描述**：获取商品详情
- **示例**：`GET /api/product/detail/1`

### 2.3 新增商品
- **接口**：`POST /api/product/add`
- **描述**：新增商品（需要管理员权限）
- **请求体**：
  ```json
  {
    "productName": "iPhone 15",
    "categoryId": 1,
    "price": 599900,
    "originalPrice": 699900,
    "stock": 100,
    "image": "http://localhost:9000/files/image/20250629/iphone15.jpg",
    "description": "苹果手机",
    "detail": "详细说明"
  }
  ```

### 2.4 更新商品
- **接口**：`PUT /api/product/update`
- **描述**：更新商品（需要管理员权限）
- **请求体**：
  ```json
  {
    "id": 1,
    "productName": "iPhone 15 Pro",
    "price": 799900
  }
  ```

### 2.5 删除商品
- **接口**：`DELETE /api/product/delete/:id`
- **描述**：删除商品（需要管理员权限）
- **示例**：`DELETE /api/product/delete/1`

### 2.6 获取热门推荐商品
- **接口**：`GET /api/home/hot`
- **描述**：获取热门推荐商品（按销量排序）
- **参数**：
  - `limit`：数量（默认 10）
- **示例**：`GET /api/home/hot?limit=10`

### 2.7 获取新品推荐商品
- **接口**：`GET /api/home/new`
- **描述**：获取新品推荐商品（按创建时间排序）
- **参数**：
  - `limit`：数量（默认 10）
- **示例**：`GET /api/home/new?limit=10`

---

## 3. 轮播图接口 (System Service - 端口 9001)

### 3.1 获取轮播图列表（前端）
- **接口**：`GET /api/home/banners`
- **描述**：获取启用的轮播图列表
- **示例**：`GET /api/home/banners`

### 3.2 获取轮播图列表（管理员）
- **接口**：`GET /api/admin/banner/list`
- **描述**：获取所有轮播图列表（管理员）
- **示例**：`GET /api/admin/banner/list`

### 3.3 新增轮播图
- **接口**：`POST /api/admin/banner/add`
- **描述**：新增轮播图（需要管理员权限）
- **请求体**：
  ```json
  {
    "title": "促销活动",
    "imageUrl": "http://localhost:9000/files/image/20250629/banner1.jpg",
    "linkUrl": "/pages/product/detail?id=1",
    "sort": 1,
    "isActive": true
  }
  ```

### 3.4 更新轮播图
- **接口**：`PUT /api/admin/banner/update`
- **描述**：更新轮播图（需要管理员权限）
- **请求体**：
  ```json
  {
    "id": 1,
    "title": "促销活动更新",
    "sort": 2
  }
  ```

### 3.5 删除轮播图
- **接口**：`DELETE /api/admin/banner/:id`
- **描述**：删除轮播图（需要管理员权限）
- **示例**：`DELETE /api/admin/banner/1`

### 3.6 启用/禁用轮播图
- **接口**：`PUT /api/admin/banner/status/:id`
- **描述**：启用/禁用轮播图（需要管理员权限）
- **参数**：
  - `isActive`：是否启用（true/false）
- **示例**：`PUT /api/admin/banner/status/1?isActive=true`

---

## 4. 地址管理接口 (System Service - 端口 9001)

### 4.1 获取地址列表
- **接口**：`GET /api/address/list`
- **描述**：获取当前客户的地址列表
- **参数**：
  - `customerId`：客户 ID
- **示例**：`GET /api/address/list?customerId=1`

### 4.2 新增地址
- **接口**：`POST /api/address/add`
- **描述**：新增收货地址
- **请求体**：
  ```json
  {
    "customerId": 1,
    "name": "张三",
    "phone": "13800138000",
    "province": "广东省",
    "city": "深圳市",
    "district": "南山区",
    "detail": "科技园路 123 号",
    "isDefault": true
  }
  ```

### 4.3 更新地址
- **接口**：`PUT /api/address/update`
- **描述**：更新收货地址
- **请求体**：
  ```json
  {
    "id": 1,
    "name": "张三",
    "phone": "13800138000",
    "detail": "科技园路 456 号"
  }
  ```

### 4.4 删除地址
- **接口**：`DELETE /api/address/:id`
- **描述**：删除收货地址
- **参数**：
  - `customerId`：客户 ID
- **示例**：`DELETE /api/address/1?customerId=1`

### 4.5 设置默认地址
- **接口**：`PUT /api/address/default/:id`
- **描述**：设置默认收货地址
- **参数**：
  - `customerId`：客户 ID
- **示例**：`PUT /api/address/default/1?customerId=1`

---

## 5. 购物车接口 (Cart Service - 端口 9004)

### 5.1 添加商品到购物车
- **接口**：`POST /api/cart/add`
- **描述**：添加商品到购物车
- **请求体**：
  ```json
  {
    "customerId": 1,
    "productId": 1,
    "quantity": 2
  }
  ```

### 5.2 获取购物车列表
- **接口**：`GET /api/cart/list`
- **描述**：获取购物车列表
- **参数**：
  - `customerId`：客户 ID
- **示例**：`GET /api/cart/list?customerId=1`

### 5.3 更新商品数量
- **接口**：`PUT /api/cart/quantity`
- **描述**：更新购物车商品数量
- **请求体**：
  ```json
  {
    "cartItemId": 1,
    "quantity": 3
  }
  ```

### 5.4 删除购物车商品
- **接口**：`DELETE /api/cart/:cartItemId`
- **描述**：删除购物车商品
- **参数**：
  - `customerId`：客户 ID
- **示例**：`DELETE /api/cart/1?customerId=1`

### 5.5 批量删除购物车商品
- **接口**：`DELETE /api/cart/batch`
- **描述**：批量删除购物车商品
- **参数**：
  - `customerId`：客户 ID
  - `cartItemIds`：购物车项 ID 列表（逗号分隔）
- **示例**：`DELETE /api/cart/batch?customerId=1&cartItemIds=1,2,3`

### 5.6 更新选中状态
- **接口**：`PUT /api/cart/selected`
- **描述**：更新购物车商品选中状态
- **请求体**：
  ```json
  {
    "cartItemId": 1,
    "selected": true,
    "customerId": 1
  }
  ```

### 5.7 全选/取消全选
- **接口**：`PUT /api/cart/selectAll`
- **描述**：全选/取消全选购物车商品
- **参数**：
  - `customerId`：客户 ID
  - `selected`：是否选中（true/false）
- **示例**：`PUT /api/cart/selectAll?customerId=1&selected=true`

### 5.8 获取购物车数量
- **接口**：`GET /api/cart/count`
- **描述**：获取购物车商品数量
- **参数**：
  - `customerId`：客户 ID
- **示例**：`GET /api/cart/count?customerId=1`

---

## 6. 订单接口 (Order Service - 端口 9006)

### 6.1 创建订单
- **接口**：`POST /api/order/create`
- **描述**：创建订单（使用分布式事务）
- **请求体**：
  ```json
  {
    "customerId": 1,
    "items": [
      {
        "productId": 1,
        "productName": "iPhone 15",
        "productImage": "http://localhost:9000/files/image/20250629/iphone15.jpg",
        "price": 599900,
        "quantity": 2
      }
    ],
    "address": "广东省深圳市南山区科技园路 123 号"
  }
  ```
- **说明**：该接口会使用 Seata 分布式事务，同时调用库存服务和支付服务

### 6.2 获取订单列表
- **接口**：`GET /api/order/list`
- **描述**：获取订单列表（分页）
- **参数**：
  - `customerId`：客户 ID
  - `status`：订单状态（可选）
    - `0`：待支付
    - `1`：已支付
    - `2`：已发货
    - `3`：已收货
    - `4`：已取消
  - `pageNum`：页码（默认 1）
  - `pageSize`：每页数量（默认 10）
- **示例**：`GET /api/order/list?customerId=1&status=0&pageNum=1&pageSize=10`

### 6.3 获取订单详情
- **接口**：`GET /api/order/:orderNo`
- **描述**：获取订单详情
- **示例**：`GET /api/order/ORD1234567890`

### 6.4 取消订单
- **接口**：`PUT /api/order/cancel`
- **描述**：取消订单（使用分布式事务）
- **请求体**：
  ```json
  {
    "orderNo": "ORD1234567890"
  }
  ```
- **说明**：该接口会使用 Seata 分布式事务，同时调用库存服务（恢复库存）和支付服务（关闭支付订单）

### 6.5 支付订单
- **接口**：`PUT /api/order/pay`
- **描述**：支付订单
- **请求体**：
  ```json
  {
    "orderNo": "ORD1234567890",
    "payType": 1
  }
  ```
- **参数**：
  - `payType`：支付方式
    - `1`：微信支付
    - `2`：支付宝支付

### 6.6 发货
- **接口**：`PUT /api/order/ship`
- **描述**：发货（需要管理员权限）
- **请求体**：
  ```json
  {
    "orderNo": "ORD1234567890",
    "trackingNo": "SF1234567890"
  }
  ```

### 6.7 确认收货
- **接口**：`PUT /api/order/confirm`
- **描述**：确认收货
- **请求体**：
  ```json
  {
    "orderNo": "ORD1234567890"
  }
  ```

---

## 7. 支付接口 (Pay Service - 端口 9005)

### 7.1 创建支付订单
- **接口**：`POST /api/pay/create`
- **描述**：创建支付订单
- **请求体**：
  ```json
  {
    "orderNo": "ORD1234567890",
    "customerId": 1,
    "amount": 1199800,
    "payType": 1
  }
  ```

### 7.2 微信支付下单
- **接口**：`POST /api/pay/wechat`
- **描述**：微信支付下单（模拟）
- **请求体**：
  ```json
  {
    "orderNo": "ORD1234567890",
    "amount": 1199800,
    "openid": "oUpF8uMuAJO_M2pxb1Q9zNjWeSs"
  }
  ```
- **响应**：
  ```json
  {
    "code": 200,
    "message": "下单成功",
    "data": {
      "orderNo": "ORD1234567890",
      "prepayId": "prepay_xxx",
      "amount": 1199800
    }
  }
  ```

### 7.3 支付宝支付下单
- **接口**：`POST /api/pay/alipay`
- **描述**：支付宝支付下单（模拟）
- **请求体**：
  ```json
  {
    "orderNo": "ORD1234567890",
    "amount": 1199800,
    "returnUrl": "http://localhost:3000/pages/order/list"
  }
  ```

### 7.4 微信支付回调
- **接口**：`POST /api/pay/wechat/callback`
- **描述**：微信支付回调接口
- **说明**：实际项目中需要验证签名

### 7.5 支付宝支付回调
- **接口**：`POST /api/pay/alipay/callback`
- **描述**：支付宝支付回调接口
- **说明**：实际项目中需要验证签名

### 7.6 查询支付状态
- **接口**：`GET /api/pay/status`
- **描述**：查询支付状态
- **参数**：
  - `payNo`：支付订单号
- **示例**：`GET /api/pay/status?payNo=PAY1234567890`

### 7.7 关闭支付订单
- **接口**：`POST /api/pay/close`
- **描述**：关闭支付订单
- **请求体**：
  ```json
  {
    "payNo": "PAY1234567890"
  }
  ```

### 7.8 退款
- **接口**：`POST /api/pay/refund`
- **描述**：退款（模拟）
- **请求体**：
  ```json
  {
    "payNo": "PAY1234567890",
    "refundAmount": 1199800,
    "reason": "商品质量问题"
  }
  ```

---

## 8. 库存接口 (Stock Service - 端口 9007)

### 8.1 查询库存
- **接口**：`GET /api/stock/:productId`
- **描述**：查询商品库存
- **示例**：`GET /api/stock/1`
- **响应**：
  ```json
  {
    "code": 200,
    "message": "查询成功",
    "data": {
      "productId": 1,
      "quantity": 100,
      "locked": 10,
      "available": 90
    }
  }
  ```

### 8.2 扣减库存
- **接口**：`PUT /api/stock/deduct`
- **描述**：扣减库存
- **请求体**：
  ```json
  {
    "productId": 1,
    "quantity": 2
  }
  ```

### 8.3 批量扣减库存
- **接口**：`PUT /api/stock/batchDeduct`
- **描述**：批量扣减库存
- **请求体**：
  ```json
  {
    "items": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 2,
        "quantity": 1
      }
    ]
  }
  ```

### 8.4 增加库存
- **接口**：`PUT /api/stock/add`
- **描述**：增加库存
- **请求体**：
  ```json
  {
    "productId": 1,
    "quantity": 50
  }
  ```

### 8.5 锁定库存
- **接口**：`POST /api/stock/lock`
- **描述**：锁定库存（下单时锁定）
- **请求体**：
  ```json
  {
    "productId": 1,
    "quantity": 2
  }
  ```

### 8.6 解锁库存
- **接口**：`POST /api/stock/unlock`
- **描述**：解锁库存（取消订单时解锁）
- **请求体**：
  ```json
  {
    "productId": 1,
    "quantity": 2
  }
  ```

---

## 9. 文件上传接口 (Upload Service - 端口 9003)

### 9.1 单文件上传
- **接口**：`POST /api/upload/single`
- **描述**：上传单个文件
- **请求体**：`multipart/form-data`
  - `file`：文件
  - `type`：文件类型（可选，如 `image`、`doc` 等）
- **示例**：
  ```bash
  curl -X POST http://localhost:9000/api/upload/single \
    -F "file=@/path/to/image.jpg" \
    -F "type=image"
  ```

### 9.2 多文件上传
- **接口**：`POST /api/upload/multiple`
- **描述**：上传多个文件
- **请求体**：`multipart/form-data`
  - `files`：文件列表
  - `type`：文件类型（可选）
- **示例**：
  ```bash
  curl -X POST http://localhost:9000/api/upload/multiple \
    -F "files=@/path/to/image1.jpg" \
    -F "files=@/path/to/image2.jpg" \
    -F "type=image"
  ```

### 9.3 文件访问
- **接口**：`GET /files/:type/:date/:filename`
- **描述**：访问上传的文件
- **示例**：`http://localhost:9000/files/image/20250629/abc123.jpg`

---

## 10. 首页接口 (System Service - 端口 9001)

### 10.1 获取首页数据
- **接口**：`GET /api/home/index`
- **描述**：获取首页全部数据（轮播图、分类、热门商品、新品推荐）
- **示例**：`GET /api/home/index`

### 10.2 获取轮播图
- **接口**：`GET /api/home/banners`
- **描述**：获取启用的轮播图列表
- **示例**：`GET /api/home/banners`

### 10.3 获取商品分类
- **接口**：`GET /api/home/categories`
- **描述**：获取商品分类列表
- **示例**：`GET /api/home/categories`

### 10.4 获取热门推荐
- **接口**：`GET /api/home/hot`
- **描述**：获取热门推荐商品
- **参数**：
  - `limit`：数量（默认 10）
- **示例**：`GET /api/home/hot?limit=10`

### 10.5 获取新品推荐
- **接口**：`GET /api/home/new`
- **描述**：获取新品推荐商品
- **参数**：
  - `limit`：数量（默认 10）
- **示例**：`GET /api/home/new?limit=10`

---

## 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200    | 成功 |
| 400    | 请求参数错误 |
| 401    | 未授权（Token 无效或过期） |
| 403    | 无权限 |
| 404    | 资源不存在 |
| 500    | 服务器内部错误 |

---

## 订单状态说明

| 状态值 | 说明 |
|--------|------|
| 0      | 待支付 |
| 1      | 已支付 |
| 2      | 已发货 |
| 3      | 已收货 |
| 4      | 已取消 |

---

## 支付状态说明

| 状态值 | 说明 |
|--------|------|
| 0      | 待支付 |
| 1      | 已支付 |
| 2      | 已关闭 |
| 3      | 已退款 |

---

## 注意事项

1. **认证接口**：除了登录、注册、首页接口外，其他接口都需要在请求头中携带 Token：
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

2. **分布式事务**：创建订单和取消订单接口使用了 Seata 分布式事务，确保数据一致性

3. **缓存策略**：商品和订单数据使用了 Redis 缓存，默认缓存 5 分钟

4. **服务注册**：所有服务都注册到 Nacos，可以通过服务名进行服务发现

5. **文件上传**：上传的文件存储在 `uploads/` 目录下，可以通过 `http://localhost:9000/files/**` 访问
