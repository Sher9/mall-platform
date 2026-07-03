import request from './request'

// 库存信息接口
export interface StockInfo {
  productId: number
  quantity: number
  available: number
}

// 获取库存信息
export function getStockInfo(productId: number) {
  return request<StockInfo>({
    url: `/stock/${productId}`,
    method: 'GET'
  })
}

// 批量获取库存
export function batchGetStock(productIds: number[]) {
  return request<StockInfo[]>({
    url: '/stock/batch',
    method: 'POST',
    data: { productIds }
  })
}
