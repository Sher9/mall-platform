import { get, post, put, del } from '@/utils/request'
import type { Customer, PageResult } from '@/types'

/** 客户列表分页参数 */
interface CustomerQuery {
  page: number
  size: number
  keyword?: string
  status?: number
}

/** 客户列表 */
export function getCustomerListApi(params: CustomerQuery): Promise<PageResult<Customer>> {
  return get<PageResult<Customer>>('/customers', {
    pageNum: params.page,
    pageSize: params.size,
    name: params.keyword
  })
}

/** 客户详情 */
export function getCustomerDetailApi(id: number): Promise<Customer> {
  return get<Customer>(`/customers/${id}`)
}

/** 新增客户 */
export function addCustomerApi(data: Partial<Customer>): Promise<Customer> {
  return post<Customer>('/customers', data)
}

/** 更新客户 */
export function updateCustomerApi(data: Partial<Customer>): Promise<Customer> {
  return put<Customer>('/customers', data)
}

/** 删除客户 */
export function deleteCustomerApi(id: number): Promise<string> {
  return del<string>(`/customers/${id}`)
}

/** 更新客户状态 */
export function updateCustomerStatusApi(id: number, status: 0 | 1): Promise<Customer> {
  return put<Customer>('/customers', { id, status })
}

/** 导出客户数据 */
export function exportCustomerApi(ids?: number[]): Promise<Blob> {
  const params = ids && ids.length > 0 ? `?ids=${ids.join(',')}` : ''
  return get<Blob>(`/customers/export${params}`, {}, { responseType: 'blob' })
}
