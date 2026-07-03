/**
 * 全局类型定义
 */

// 通用响应结果
export interface Result<T = any> {
  code: number
  message: string
  data: T
}

// 分页结果
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 商品类型
export interface Product {
  productId: number
  productNo: string
  productName: string
  category: string
  brand: string
  price: number
  unit: string
  description: string
  imageUrl: string
  status: number
  deleted: number
  createTime: string
  updateTime: string
}

// 客户类型
export interface Customer {
  customerId: number
  username: string
  phone: string
  nickname: string
  avatar: string
  gender: number
  email: string
  status: number
  createTime: string
}

// 订单类型
export interface OrderInfo {
  orderId: number
  orderNo: string
  customerId: number
  productId: number
  quantity: number
  totalAmount: number
  status: number
  createTime: string
  payTime: string
  product?: Product
}

// 购物车项
export interface CartItem {
  id: string
  productId: number
  productName: string
  price: number
  quantity: number
  imageUrl: string
  checked: boolean
}

// 登录表单
export interface LoginForm {
  phone: string
  password: string
}

// 注册表单
export interface RegisterForm {
  phone: string
  password: string
  confirmPassword: string
  nickname: string
}

// 用户状态
export interface UserState {
  token: string | null
  userInfo: Customer | null
}
