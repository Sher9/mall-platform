import request from './request'

// 商品查询参数接口
export interface ProductQueryParams {
  pageNum?: number
  pageSize?: number
  category?: string
  keyword?: string
}

// 商品接口
export interface Product {
  productId: number
  productNo: string
  productName: string
  category: string
  brand: string
  price: number
  unit: string
  description: string
  imageUrl: string
  status: number
  deleted: number
  createTime: string
  updateTime: string
}

// 分页响应接口
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 获取商品列表
export function getProductList(params: ProductQueryParams) {
  return request<PageResult<Product>>({
    url: '/system/product/list',
    method: 'GET',
    params
  })
}

// 获取商品详情
export function getProductDetail(id: number) {
  return request<Product>({
    url: `/system/product/${id}`,
    method: 'GET'
  })
}

// 获取商品分类
export function getProductCategories() {
  return request<string[]>({
    url: '/system/product/categories',
    method: 'GET'
  })
}
