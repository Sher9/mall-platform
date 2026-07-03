import { get, post, put, del } from '@/utils/request'

export interface Category {
  categoryId?: number
  categoryName: string
  parentId?: number
  sortOrder?: number
  status?: number
  deleted?: number
  createTime?: string
  updateTime?: string
}

/** 获取分类分页列表 */
export function getCategoryPageApi(params: {
  pageNum: number
  pageSize: number
  categoryName?: string
  status?: number
}) {
  return get<{ records: Category[]; total: number }>('/categories/page', params)
}

/** 获取所有分类 */
export function getCategoryAllApi() {
  return get<Category[]>('/categories/all')
}

/** 获取分类详情 */
export function getCategoryDetailApi(id: number) {
  return get<Category>(`/categories/${id}`)
}

/** 新增分类 */
export function addCategoryApi(data: Category) {
  return post<Category>('/categories', data)
}

/** 更新分类 */
export function updateCategoryApi(id: number, data: Category) {
  return put<Category>(`/categories/${id}`, data)
}

/** 删除分类 */
export function deleteCategoryApi(id: number) {
  return del(`/categories/${id}`)
}

/** 启用/停用分类 */
export function updateCategoryStatusApi(id: number, status: number) {
  return put(`/categories/${id}/status?status=${status}`)
}
