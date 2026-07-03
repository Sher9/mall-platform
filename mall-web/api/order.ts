/**
 * 订单相关API接口
 */
import type { OrderInfo, PageResult } from '~/types'
import { http } from '~/utils/request'

/**
 * 获取订单列表
 */
export async function getOrderList(params: {
  pageNum?: number
  pageSize?: number
  orderNo?: string
}) {
  return await http.get<PageResult<OrderInfo>>('/order', params)
}

/**
 * 获取订单详情
 */
export async function getOrderDetail(orderId: number) {
  return await http.get<OrderInfo>(`/order/${orderId}`)
}

/**
 * 创建订单
 */
export async function createOrder(data: Partial<OrderInfo>) {
  return await http.post<OrderInfo>('/order', data)
}

/**
 * 支付订单
 */
export async function payOrder(orderId: number) {
  return await http.put<OrderInfo>(`/order/${orderId}/pay`)
}

/**
 * 取消订单
 */
export async function cancelOrder(orderId: number) {
  return await http.put<OrderInfo>(`/order/${orderId}/cancel`)
}

/**
 * 确认收货
 */
export async function receiveOrder(orderId: number) {
  return await http.put<OrderInfo>(`/order/${orderId}/receive`)
}
