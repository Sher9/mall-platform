import { post, get } from '@/utils/request'
import type { LoginData, LoginResult, UserInfo } from '@/types'

/** 管理员登录 */
export function loginApi(data: LoginData): Promise<LoginResult> {
  return post<LoginResult>('/admin/login', data)
}

/** 获取当前登录用户信息 */
export function getUserInfoApi(): Promise<UserInfo> {
  return get<UserInfo>('/user/info')
}

/** 退出登录 */
export function logoutApi(username: string): Promise<string> {
  return post<string>(`/admin/logout?username=${encodeURIComponent(username)}`)
}
