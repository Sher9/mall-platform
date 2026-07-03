import request from './request'

// 管理后台登录
export function adminLogin(data) {
  return request.post('/admin/login', data)
}

// 管理后台登出
export function adminLogout() {
  return request.post('/admin/logout')
}

// 获取当前用户信息
export function getAdminInfo(username) {
  return request.get('/admin/info', { params: { username } })
}
