/**
 * 首页相关API接口
 * 直接使用 $fetch 避免 store 调用时机问题
 */
import type { Result } from '~/types'

/**
 * 获取轮播图列表
 */
export async function getBannerList() {
  try {
    const data = await $fetch<Result<any[]>>('/api/banner/list')
    return data.data || []
  } catch (error) {
    console.error('获取轮播图失败', error)
    throw error
  }
}

/**
 * 获取热门商品（按销量排序，取前4条）
 * @deprecated 请使用 product.ts 中的 getHotProducts
 */
export async function getHotProducts() {
  try {
    const data = await $fetch<Result<any[]>>('/api/product/hot')
    return data.data || []
  } catch (error) {
    console.error('获取热门商品失败', error)
    throw error
  }
}

/**
 * 获取新品推荐（按创建时间排序，取前3条）
 * @deprecated 请使用 product.ts 中的 getNewProducts
 */
export async function getNewProducts() {
  try {
    const data = await $fetch<Result<any[]>>('/api/product/new')
    return data.data || []
  } catch (error) {
    console.error('获取新品失败', error)
    throw error
  }
}
