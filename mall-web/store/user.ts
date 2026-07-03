/**
 * 用户状态管理
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Customer, RegisterForm } from '~/types'
import { login as loginApi, register as registerApi, getUserInfo as getUserInfoApi } from '~/api/auth'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string | null>(process.client ? localStorage.getItem('token') : null)
  const userInfo = ref<Customer | null>(null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)

  // 设置 token
  const setToken = (newToken: string) => {
    token.value = newToken
    if (process.client) {
      localStorage.setItem('token', newToken)
    }
  }

  // 登录
  const login = async (phone: string, password: string) => {
    const result = await loginApi({ phone, password })
    setToken(result.token)
    await initUserInfo()
    return result
  }

  // 注册
  const register = async (phone: string, password: string, nickname: string) => {
    return await registerApi({ phone, password, confirmPassword: password, nickname } as RegisterForm)
  }

  // 初始化用户信息
  const initUserInfo = async () => {
    if (!token.value) return

    try {
      const result = await getUserInfoApi()
      userInfo.value = result
    } catch (error) {
      console.error('获取用户信息失败', error)
    }
  }

  // 退出登录
  const logout = () => {
    token.value = null
    userInfo.value = null
    if (process.client) {
      localStorage.removeItem('token')
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    login,
    register,
    initUserInfo,
    logout
  }
})
