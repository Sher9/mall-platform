/**
 * 认证相关 composable
 * 内部调用 api/ 下的接口函数
 */
import type { LoginForm, RegisterForm, Customer } from '~/types'
import { login as loginApi, register as registerApi, logout as logoutApi, getUserInfo as getUserInfoApi } from '~/api/auth'

export const useAuth = () => {
  // 登录
  const login = async (data: LoginForm) => {
    return await loginApi(data)
  }

  // 注册
  const register = async (data: RegisterForm) => {
    return await registerApi(data)
  }

  // 登出
  const logout = async (phone: string) => {
    await logoutApi(phone)
  }

  // 获取用户信息
  const getUserInfo = async () => {
    return await getUserInfoApi()
  }

  return {
    login,
    register,
    logout,
    getUserInfo
  }
}
