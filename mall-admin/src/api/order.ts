import { get, post, put, del } from '@/utils/request'
import type { Order, PageResult } from '@/types'

/** 订单列表分页参数 */
interface OrderQuery {
  page: number
  size: number
  orderNo?: string
  status?: number
}

/** 订单列表 */
export function getOrderListApi(params: OrderQuery): Promise<PageResult<Order>> {
  return get<PageResult<Order>>('/order', {
    pageNum: params.page,
    pageSize: params.size,
    orderNo: params.orderNo,
    status: params.status
  })
}

/** 订单详情 */
export function getOrderDetailApi(id: number): Promise<Order> {
  return get<Order>(`/order/${id}`)
}

/** 更新订单 */
export function updateOrderApi(data: Partial<Order>): Promise<Order> {
  return put<Order>('/order', data)
}

/** 删除订单 */
export function deleteOrderApi(id: number): Promise<void> {
  return del<void>(`/order/${id}`)
}

/** 支付订单 */
export function payOrderApi(id: number): Promise<Order> {
  return put<Order>(`/order/${id}/pay`)
}

/** 发货 */
export function shipOrderApi(id: number): Promise<Order> {
  return put<Order>(`/order/${id}/ship`)
}

/** 确认收货 */
export function receiveOrderApi(id: number): Promise<Order> {
  return put<Order>(`/order/${id}/receive`)
}

/** 取消订单 */
export function cancelOrderApi(id: number): Promise<Order> {
  return put<Order>(`/order/${id}/cancel`)
}

/** 导出订单数据 */
export function exportOrderApi(ids?: number[]): Promise<Blob> {
  const params = ids && ids.length > 0 ? `?ids=${ids.join(',')}` : ''
  return get<Blob>(`/order/export${params}`, {}, { responseType: 'blob' })
}
