import request from './request'

// 分页查询商品
export function getProductList(params) {
  return request.get('/products', { params })
}

// 获取所有商品
export function getAllProducts() {
  return request.get('/products/all')
}

// 根据 ID 查询商品
export function getProductById(id) {
  return request.get(`/products/${id}`)
}

// 新增商品
export function createProduct(data) {
  return request.post('/products', data)
}

// 更新商品
export function updateProduct(id, data) {
  return request.put(`/products/${id}`, data)
}

// 删除商品
export function deleteProduct(id) {
  return request.delete(`/products/${id}`)
}
