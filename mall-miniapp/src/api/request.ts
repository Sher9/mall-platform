// 请求基础 URL（根据环境切换）
const BASE_URL = 'http://localhost:8000/api'

// 请求配置接口
interface RequestConfig {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  params?: any
  headers?: Record<string, string>
  timeout?: number
}

// 响应数据接口
interface ResponseData<T = any> {
  code: number
  message: string
  data: T
}

// 通用请求封装（使用 uni.request，兼容小程序）
function request<T = any>(config: RequestConfig): Promise<ResponseData<T>> {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    uni.request({
      url: `${BASE_URL}${config.url}`,
      method: config.method || 'GET',
      data: config.data || config.params || {},
      header: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...config.headers
      },
      timeout: config.timeout || 15000,
      success: (res: any) => {
        const data: ResponseData<T> = res.data
        if (data.code === 200 || data.code === 0) {
          resolve(data)
        } else if (data.code === 401) {
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')
          uni.showToast({ title: '登录已过期', icon: 'none' })
          setTimeout(() => {
            uni.reLaunch({ url: '/pages/login/login' })
          }, 1500)
          reject(new Error(data.message || '登录已过期'))
        } else {
          uni.showToast({ title: data.message || '请求失败', icon: 'none' })
          reject(new Error(data.message || '请求失败'))
        }
      },
      fail: (err: any) => {
        uni.showToast({ title: '网络异常', icon: 'none' })
        reject(err)
      }
    })
  })
}

export default request
export const get = <T = any>(url: string, params?: any) => request<T>({ url, method: 'GET', params })
export const post = <T = any>(url: string, data?: any) => request<T>({ url, method: 'POST', data })
export const put = <T = any>(url: string, data?: any) => request<T>({ url, method: 'PUT', data })
export const del = <T = any>(url: string, data?: any) => request<T>({ url, method: 'DELETE', data })
