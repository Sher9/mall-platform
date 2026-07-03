/**
 * 订单相关 composable
 * 内部调用 api/ 下的接口函数
 */
import type { OrderInfo, PageResult } from '~/types'
import { 
  getOrderList as getOrderListApi, 
  getOrderDetail as getOrderDetailApi,
  createOrder as createOrderApi,
  payOrder as payOrderApi,
  cancelOrder as cancelOrderApi,
  receiveOrder as receiveOrderApi
} from '~/api/order'

export const useOrder = () => {
  // 获取订单列表
  const getOrderList = async (params: {
    pageNum?: number
    pageSize?: number
    orderNo?: string
  }) => {
    return await getOrderListApi(params)
  }

  // 获取订单详情
  const getOrderDetail = async (orderId: number) => {
    return await getOrderDetailApi(orderId)
  }

  // 创建订单
  const createOrder = async (data: Partial<OrderInfo>) => {
    return await createOrderApi(data)
  }

  // 支付订单
  const payOrder = async (orderId: number) => {
    return await payOrderApi(orderId)
  }

  // 取消订单
  const cancelOrder = async (orderId: number) => {
    return await cancelOrderApi(orderId)
  }

  // 确认收货
  const receiveOrder = async (orderId: number) => {
    return await receiveOrderApi(orderId)
  }

  return {
    getOrderList,
    getOrderDetail,
    createOrder,
    payOrder,
    cancelOrder,
    receiveOrder
  }
}
