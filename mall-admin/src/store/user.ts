import { defineStore } from 'pinia'
import { loginApi, getUserInfoApi, logoutApi } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'
import type { LoginData, UserInfo } from '@/types'

interface UserState {
  token: string
  userInfo: UserInfo | null
  permissions: string[]
  roles: string[]
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: getToken() || '',
    userInfo: null,
    permissions: [],
    roles: []
  }),

  getters: {
    isLogin: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    nickname: (state) => state.userInfo?.nickname || '管理员',
    avatar: (state) => state.userInfo?.avatar || ''
  },

  actions: {
    async login(loginData: LoginData) {
      const data = await loginApi(loginData)
      this.token = data.accessToken
      setToken(data.accessToken)

      // 直接使用登录返回的 userInfo，无需再调一次接口
      this.userInfo = {
        id: data.userId,
        username: data.username,
        nickname: data.nickname,
        avatar: data.avatar || '',
        phone: data.phone || '',
        email: data.email || '',
        roles: data.roles || [],
        permissions: data.permissions || []
      }
      this.roles = data.roles || []
      this.permissions = data.permissions || []
    },

    async fetchUserInfo() {
      const data = await getUserInfoApi()
      this.userInfo = data
      this.permissions = data.permissions || []
      this.roles = data.roles || []
    },

    async logout() {
      try {
        await logoutApi(this.userInfo ? this.userInfo.username : "")
      } finally {
        this.token = ''
        this.userInfo = null
        this.permissions = []
        this.roles = []
        removeToken()
      }
    },

    resetToken() {
      this.token = ''
      this.userInfo = null
      this.permissions = []
      this.roles = []
      removeToken()
    }
  },

  persist: {
    key: 'mall-admin-user',
    pick: ['token', 'userInfo', 'roles', 'permissions']
  }
})
