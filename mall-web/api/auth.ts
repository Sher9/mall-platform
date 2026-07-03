/**
 * 认证相关API接口
 */
import type { LoginForm, RegisterForm, Customer } from '~/types'
import { http } from '~/utils/request'

/**
 * 登录
 */
export async function login(data: LoginForm) {
  return await http.post<{ token: string; tokenType: string }>('/customer/login', data)
}

/**
 * 注册
 */
export async function register(data: RegisterForm) {
  return await http.post<Customer>('/customer/register', data)
}

/**
 * 登出
 */
export async function logout(phone: string) {
  await http.post('/customer/logout', { phone })
}

/**
 * 获取用户信息
 */
export async function getUserInfo() {
  return await http.get<Customer>('/customer/info')
}
