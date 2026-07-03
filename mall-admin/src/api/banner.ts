import { get, post, put, del } from '@/utils/request'
import type { Banner, PageResult } from '@/types'

/** 轮播图列表 */
export function getBannerListApi(): Promise<PageResult<Banner>> {
  return get<PageResult<Banner>>('/banner/list')
}

/** 轮播图详情 */
export function getBannerDetailApi(id: number): Promise<Banner> {
  return get<Banner>(`/banner/${id}`)
}

/** 新增轮播图 */
export function addBannerApi(data: Partial<Banner>): Promise<Banner> {
  return post<Banner>('/banner/add', data)
}

/** 更新轮播图 */
export function updateBannerApi(data: Partial<Banner>): Promise<void> {
  return put<void>('/banner/update', data)
}

/** 删除轮播图 */
export function deleteBannerApi(id: number): Promise<void> {
  return del<void>(`/banner/${id}`)
}

/** 启用/禁用轮播图 */
export function updateBannerStatusApi(id: number, isActive: boolean): Promise<void> {
  return put<void>(`/banner/status/${id}`, null, {
    params: { isActive }
  })
}
