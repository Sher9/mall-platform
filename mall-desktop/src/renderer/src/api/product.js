import request from './request'

// 分页查询商品
export function getProductList(params) {
  return request.get('/product', { params })
}

// 获取所有商品
export function getAllProducts() {
  return request.get('/product/all')
}

// 根据 ID 查询商品
export function getProductById(id) {
  return request.get(`/product/${id}`)
}

// 新增商品
export function createProduct(data) {
  return request.post('/product', data)
}

// 更新商品
export function updateProduct(id, data) {
  return request.put(`/product/${id}`, data)
}

// 删除商品
export function deleteProduct(id) {
  return request.delete(`/product/${id}`)
}
