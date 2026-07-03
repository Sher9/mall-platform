/** 通用接口响应结构 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

/** 分页请求参数 */
export interface PageParams {
  page: number
  size: number
  [key: string]: any
}

/** 分页响应结构 */
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

/** 用户信息（登录者） */
export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar?: string
  phone?: string
  email?: string
  roles: string[]
  permissions: string[]
}

/** 系统用户（管理后台登录者） */
export interface SystemUser {
  id: number
  username: string
  password?: string
  nickname: string
  avatar?: string
  phone?: string
  email?: string
  status: 0 | 1             // 0=禁用 1=启用
  roles: string[]            // 角色编码列表
  roleNames?: string[]       // 角色名称列表
  lastLoginTime?: string
  lastLoginIp?: string
  createTime: string
  updateTime?: string
}

/** 客户信息（商城小程序用户，对应后端 Customer 实体） */
export interface Customer {
  id: number
  username: string
  password?: string
  nickname: string
  avatar?: string
  phone?: string
  email?: string
  gender?: 0 | 1 | 2        // 0=未知 1=男 2=女
  birthday?: string
  status: 0 | 1             // 0=禁用 1=启用
  lastLoginTime?: string
  lastLoginIp?: string
  registerTime?: string
  createTime: string
  updateTime?: string
}

/** 登录请求 */
export interface LoginData {
  username: string
  password: string
  captcha?: string
}

/** 登录响应 */
export interface LoginResult {
  accessToken: string
  tokenType: string
  userId: number
  username: string
  nickname: string
  avatar?: string
  email?: string
  phone?: string
  roles: string[]
  permissions: string[]
}

/** 商品 */
export interface Product {
  productId?: number
  productNo?: string
  productName: string
  category?: string
  brand?: string
  price: number
  unit?: string
  description?: string
  imageUrl?: string
  sales?: number
  status?: number
  stockCount?: number
  createTime?: string
  updateTime?: string
}

/** 订单 */
export interface Order {
  id: number
  orderNo: string
  userId: number
  username?: string
  totalAmount: number
  status: 0 | 1 | 2 | 3 | 4 | 5
  statusText?: string
  items?: OrderItem[]
  address?: string
  createTime: string
  payTime?: string
  shipTime?: string
}

/** 订单明细 */
export interface OrderItem {
  id: number
  orderId: number
  productId: number
  productName: string
  productImage?: string
  quantity: number
  price: number
}

/** 菜单路由 */
export interface MenuItem {
  id: number
  parentId: number
  name: string
  path: string
  component?: string
  icon?: string
  sort: number
  hidden: boolean
  children?: MenuItem[]
}

/** 系统菜单（对应后端 Menu 实体） */
export interface Menu {
  menuId: number
  parentId: number
  menuName: string
  menuType: number   // 1=目录 2=菜单 3=按钮
  path?: string
  component?: string
  perms?: string
  icon?: string
  sort?: number
  visible?: number
  status?: number
  createTime?: string
  updateTime?: string
  children?: Menu[]
}

/** 轮播图 */
export interface Banner {
  id?: number
  title: string
  imageUrl: string
  linkUrl: string
  sort: number
  isActive: boolean
  createdAt?: string
  updatedAt?: string
}
