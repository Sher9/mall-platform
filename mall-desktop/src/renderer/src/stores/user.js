import { defineStore } from 'pinia'
import { ref } from 'vue'
import { adminLogin, adminLogout, getAdminInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  async function login(loginForm) {
    const res = await adminLogin(loginForm)
    token.value = res.data
    localStorage.setItem('token', res.data)
    return res
  }

  async function fetchUserInfo(username) {
    const res = await getAdminInfo(username)
    userInfo.value = res.data
    return res.data
  }

  async function logout() {
    try {
      await adminLogout()
    } finally {
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
    }
  }

  return { token, userInfo, login, fetchUserInfo, logout }
})
