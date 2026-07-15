// 分页查询订单
export function getOrderList(params) {
  return request.get('/order', { params })
}

// 根据 ID 查询订单
export function getOrderById(id) {
  return request.get(`/order/${id}`)
}

// 创建订单
export function createOrder(data) {
  return request.post('/order', data)
}

// 支付订单
export function payOrder(id) {
  return request.put(`/order/${id}/pay`)
}

// 发货订单
export function shipOrder(id) {
  return request.put(`/order/${id}/ship`)
}

// 确认收货
export function receiveOrder(id) {
  return request.put(`/order/${id}/receive`)
}

// 取消订单
export function cancelOrder(id) {
  return request.put(`/order/${id}/cancel`)
}

// 删除订单
export function deleteOrder(id) {
  return request.delete(`/order/${id}`)
}
