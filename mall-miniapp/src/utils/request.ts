// 请求配置基础URL（根据环境配置）
const BASE_URL = '/api'

// 请求拦截器类型
type RequestInterceptor = (config: any) => any

// 响应拦截器类型
type ResponseInterceptor = (response: any) => any
type ErrorInterceptor = (error: any) => any

// 请求配置
interface RequestConfig {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: any
  timeout?: number
}

// 响应数据
interface ResponseData {
  code: number
  data: any
  message?: string
}

// 创建请求实例
class Request {
  private baseURL: string
  private timeout: number
  private requestInterceptors: RequestInterceptor[]
  private responseInterceptors: ResponseInterceptor[]
  private errorInterceptors: ErrorInterceptor[]

  constructor() {
    this.baseURL = BASE_URL
    this.timeout = 10000
    this.requestInterceptors = []
    this.responseInterceptors = []
    this.errorInterceptors = []
  }

  // 添加请求拦截器
  useRequestInterceptor(interceptor: RequestInterceptor) {
    this.requestInterceptors.push(interceptor)
  }

  // 添加响应拦截器
  useResponseInterceptor(interceptor: ResponseInterceptor) {
    this.responseInterceptors.push(interceptor)
  }

  // 添加错误拦截器
  useErrorInterceptor(interceptor: ErrorInterceptor) {
    this.errorInterceptors.push(interceptor)
  }

  // 发送请求
  async request(config: RequestConfig): Promise<any> {
    // 执行请求拦截器
    let processedConfig = { ...config }
    for (const interceptor of this.requestInterceptors) {
      processedConfig = interceptor(processedConfig) || processedConfig
    }

    // 构建完整URL
    const url = processedConfig.url.startsWith('http') 
      ? processedConfig.url 
      : `${this.baseURL}${processedConfig.url}`

    // 构建请求头
    const header = {
      'Content-Type': 'application/json',
      ...processedConfig.header
    }

    try {
      // 使用 uni.request 发送请求
      const response = await new Promise<any>((resolve, reject) => {
        uni.request({
          url,
          method: processedConfig.method || 'GET',
          data: processedConfig.data,
          header,
          timeout: processedConfig.timeout || this.timeout,
          success: (res) => {
            resolve(res)
          },
          fail: (err) => {
            reject(err)
          }
        })
      })

      // 执行响应拦截器
      let processedResponse = response
      for (const interceptor of this.responseInterceptors) {
        processedResponse = interceptor(processedResponse) || processedResponse
      }

      return processedResponse
    } catch (error) {
      // 执行错误拦截器
      let processedError = error
      for (const interceptor of this.errorInterceptors) {
        processedError = interceptor(processedError) || processedError
      }
      throw processedError
    }
  }

  // GET 请求
  get(url: string, data?: any, config?: Partial<RequestConfig>): Promise<any> {
    return this.request({
      url,
      method: 'GET',
      data,
      ...config
    })
  }

  // POST 请求
  post(url: string, data?: any, config?: Partial<RequestConfig>): Promise<any> {
    return this.request({
      url,
      method: 'POST',
      data,
      ...config
    })
  }

  // PUT 请求
  put(url: string, data?: any, config?: Partial<RequestConfig>): Promise<any> {
    return this.request({
      url,
      method: 'PUT',
      data,
      ...config
    })
  }

  // DELETE 请求
  delete(url: string, data?: any, config?: Partial<RequestConfig>): Promise<any> {
    return this.request({
      url,
      method: 'DELETE',
      data,
      ...config
    })
  }
}

// 创建请求实例
const request = new Request()

// 添加请求拦截器（添加token）
request.useRequestInterceptor((config) => {
  try {
    const token = uni.getStorageSync('token')
    if (token) {
      if (!config.header) {
        config.header = {}
      }
      config.header.Authorization = `Bearer ${token}`
    }
  } catch (e) {
    console.log('获取token失败:', e)
  }
  return config
})

// 添加响应拦截器（处理业务错误）
request.useResponseInterceptor((response) => {
  const res = response.data
  
  // 如果返回的状态码不是200，说明有错误
  if (res.code !== 200) {
    uni.showToast({
      title: res.message || '请求失败',
      icon: 'none'
    })
    return Promise.reject(new Error(res.message || '请求失败'))
  }
  
  return res
})

// 添加错误拦截器（处理网络错误）
request.useErrorInterceptor((error) => {
  console.error('请求错误:', error)
  let message = '网络错误'
  if (error.message) {
    message = error.message
  }
  uni.showToast({
    title: message,
    icon: 'none'
  })
  return Promise.reject(error)
})

export default request
