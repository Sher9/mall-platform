/**
 * HTTP 请求工具
 * 基于 $fetch (Nuxt 3 内置) 封装
 */
import type { Result } from '~/types'
import { useUserStore } from '@/store/user'

const BASE_URL = '/api'

// 请求拦截器
async function request<T>(url: string, options: any = {}): Promise<T> {
  const userStore = useUserStore()
  const { $router } = useNuxtApp()

  // 默认配置
  const config: any = {
    baseURL: BASE_URL,
    headers: {},
    ...options
  }

  // 添加 token
  if (userStore.token) {
    config.headers = {
      ...config.headers,
      Authorization: `Bearer ${userStore.token}`
    }
  }

  try {
    const response = await $fetch<Result<T>>(url, config)

    // 业务成功
    if (response.code === 200 || response.code === 0) {
      return response.data as T
    }

    // Token 过期
    if (response.code === 401) {
      userStore.logout()
      await $router?.push('/login')
      throw new Error(response.message || '请先登录')
    }

    throw new Error(response.message || '请求失败')
  } catch (error: any) {
    // 网络错误
    if (error?.response?._data) {
      const data = error.response._data
      throw new Error(data.message || '服务器错误')
    }
    throw error
  }
}

// 封装常用方法
export const http = {
  get<T>(url: string, params?: Record<string, any>): Promise<T> {
    return request<T>(url, { method: 'GET', params })
  },

  post<T>(url: string, body?: any): Promise<T> {
    return request<T>(url, { method: 'POST', body })
  },

  put<T>(url: string, body?: any): Promise<T> {
    return request<T>(url, { method: 'PUT', body })
  },

  delete<T>(url: string): Promise<T> {
    return request<T>(url, { method: 'DELETE' })
  }
}
