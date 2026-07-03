import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/api/request'

// 用户信息接口
export interface UserInfo {
  customerId: number
  username: string
  phone: string
  nickname: string
  avatar: string
  gender: number
  email: string
  status: number
  createTime: string
  accessToken?: string
}

// 登录表单接口
export interface LoginForm {
  phone: string
  password: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(uni.getStorageSync('token') || '')
  const userInfo = ref<UserInfo | null>(
    uni.getStorageSync('userInfo') ? JSON.parse(uni.getStorageSync('userInfo')) : null
  )

  const isLoggedIn = computed(() => !!token.value)

  // 登录
  async function loginAction(loginData: LoginForm) {
    const res = await request<{ accessToken: string } & UserInfo>({
      url: '/auth/login',
      method: 'POST',
      data: loginData
    })
    const data = res.data
    token.value = data.accessToken
    userInfo.value = data
    uni.setStorageSync('token', data.accessToken)
    uni.setStorageSync('userInfo', JSON.stringify(data))
    return data
  }

  // 登出
  function logoutAction() {
    token.value = ''
    userInfo.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
    uni.reLaunch({ url: '/pages/login/login' })
  }

  // 获取用户信息
  async function fetchUserInfo() {
    if (!token.value) return
    try {
      const res = await request<UserInfo>({
        url: '/system/user/info',
        method: 'GET'
      })
      userInfo.value = res.data
      uni.setStorageSync('userInfo', JSON.stringify(userInfo.value))
    } catch (e) {
      console.error('获取用户信息失败', e)
    }
  }

  return { token, userInfo, isLoggedIn, loginAction, logoutAction, fetchUserInfo }
})
