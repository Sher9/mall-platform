import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { adminLogin, adminLogout, getAdminInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  async function login(loginForm) {
    const res = await adminLogin(loginForm)
    const data = res.data
    // 后端登录响应字段为 accessToken（非 token）
    const accessToken = data.accessToken || data.token
    token.value = accessToken
    localStorage.setItem('token', accessToken)
    userInfo.value = {
      userId: data.userId,
      username: data.username,
      nickname: data.nickname || data.username
    }
    return res
  }

  async function fetchUserInfo() {
    const res = await getAdminInfo()
    if (res.data) {
      userInfo.value = {
        userId: res.data.userId,
        username: res.data.username,
        nickname: res.data.nickname || res.data.username
      }
    }
    return res.data
  }

  async function logout() {
    try {
      if (userInfo.value?.username) {
        await adminLogout(userInfo.value.username)
      }
    } catch (e) {
      // 忽略退出接口异常，保证本地状态清空
    } finally {
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
    }
  }

  return { token, userInfo, isLoggedIn, login, fetchUserInfo, logout }
})
