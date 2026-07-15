import request from './request'

// 管理后台登录
export function adminLogin(data) {
  return request.post('/admin/login', data)
}

// 管理后台登出（后端需要 username 参数）
export function adminLogout(username) {
  return request.post('/admin/logout', null, { params: { username } })
}

// 获取当前用户信息（依赖网关注入的 X-User-Name 请求头）
export function getAdminInfo() {
  return request.get('/admin/info')
}
