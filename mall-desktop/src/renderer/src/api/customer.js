import request from './request'

// 分页查询客户
export function getCustomerList(params) {
  return request.get('/customers', { params })
}

// 获取所有客户
export function getAllCustomers() {
  return request.get('/customers/all')
}

// 根据 ID 查询客户
export function getCustomerById(id) {
  return request.get(`/customers/${id}`)
}

// 新增客户
export function createCustomer(data) {
  return request.post('/customers', data)
}

// 更新客户
export function updateCustomer(id, data) {
  return request.put(`/customers/${id}`, data)
}

// 删除客户
export function deleteCustomer(id) {
  return request.delete(`/customers/${id}`)
}
