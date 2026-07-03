import { get, post, put, del } from '@/utils/request'
import type { Product, PageResult } from '@/types'

/** 商品列表分页参数 */
interface ProductQuery {
  page: number
  size: number
  name?: string
  status?: number
}

/** 商品列表 */
export function getProductListApi(params: ProductQuery): Promise<PageResult<Product>> {
  return get<PageResult<Product>>('/product', {
    pageNum: params.page,
    pageSize: params.size,
    productName: params.name
  })
}

/** 商品详情 */
export function getProductDetailApi(id: number): Promise<Product> {
  return get<Product>(`/product/${id}`)
}

/** 新增商品 */
export function addProductApi(data: Partial<Product>): Promise<Product> {
  return post<Product>('/product', data)
}

/** 更新商品 */
export function updateProductApi(data: Partial<Product>): Promise<Product> {
  return put<Product>('/product', data)
}

/** 删除商品 */
export function deleteProductApi(id: number): Promise<void> {
  return del<void>(`/product/${id}`)
}
