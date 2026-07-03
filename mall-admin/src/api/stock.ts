import { get, post, put, del } from '@/utils/request'
import type { PageResult } from '@/types'

/** 库存列表分页参数 */
interface StockQuery {
  page: number
  size: number
  productName?: string
}

/** 库存列表 */
export function getStockListApi(params: StockQuery): Promise<PageResult<any>> {
  return get<PageResult<any>>('/stock', {
    pageNum: params.page,
    pageSize: params.size,
    productName: params.productName
  })
}

/** 库存详情 */
export function getStockDetailApi(id: number): Promise<any> {
  return get<any>(`/stock/${id}`)
}

/** 新增库存 */
export function addStockApi(data: any): Promise<any> {
  return post<any>('/stock', data)
}

/** 更新库存 */
export function updateStockApi(data: any): Promise<any> {
  return put<any>('/stock', data)
}

/** 删除库存 */
export function deleteStockApi(id: number): Promise<void> {
  return del<void>(`/stock/${id}`)
}

/** 入库 */
export function increaseStockApi(data: any): Promise<any> {
  return post<any>('/stock/increase', data)
}

/** 出库 */
export function decreaseStockApi(data: any): Promise<any> {
  return post<any>('/stock/decrease', data)
}

/** 库存日志 */
export function getStockLogsApi(productId?: number): Promise<any[]> {
  return get<any[]>('/stock/logs', { productId })
}

/** 导出库存数据 */
export function exportStockApi(ids?: number[]): Promise<Blob> {
  const params = ids && ids.length > 0 ? `?ids=${ids.join(',')}` : ''
  return get<Blob>(`/stock/export${params}`, {}, { responseType: 'blob' })
}
