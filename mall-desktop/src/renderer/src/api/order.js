import request from './request'

// 分页查询订单
export function getOrderList(params) {
  return request.get('/orders', { params })
}

// 根据 ID 查询订单
export function getOrderById(id) {
  return request.get(`/orders/${id}`)
}

// 创建订单
export function createOrder(data) {
  return request.post('/orders', data)
}

// 更新订单状态
export function updateOrderStatus(id, status) {
  return request.put(`/orders/${id}/status`, { status })
}

// 删除订单
export function deleteOrder(id) {
  return request.delete(`/orders/${id}`)
}
