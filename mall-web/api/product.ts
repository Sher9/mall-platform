/**
 * 商品相关API接口
 */
import type { Product, PageResult } from '~/types'
import { http } from '~/utils/request'

/**
 * 获取商品分页列表
 */
export async function getProductList(params: {
  pageNum?: number
  pageSize?: number
  productName?: string
  category?: string
}) {
  return await http.get<PageResult<Product>>('/product', params)
}

/**
 * 获取商品详情
 */
export async function getProductDetail(productId: number) {
  return await http.get<Product>(`/product/${productId}`)
}

/**
 * 获取所有商品
 */
export async function getAllProducts() {
  return await http.get<Product[]>('/product/all')
}
