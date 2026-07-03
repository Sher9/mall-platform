import request from '@/utils/request'

/**
 * 获取轮播图列表
 */
export function getBannerList() {
  return request({
    url: '/banner/list',
    method: 'get'
  })
}

/**
 * 获取分类列表
 */
export function getCategoryList() {
  return request({
    url: '/categories/all',
    method: 'get'
  })
}

/**
 * 获取热门商品
 */
export function getHotProducts() {
  return request({
    url: '/product/hot',
    method: 'get'
  })
}

/**
 * 获取新品上市
 */
export function getNewProducts() {
  return request({
    url: '/product/new',
    method: 'get'
  })
}
