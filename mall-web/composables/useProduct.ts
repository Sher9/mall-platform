/**
 * 商品相关 composable
 * 内部调用 api/ 下的接口函数
 */
import type { Product, PageResult } from '~/types'
import { 
  getProductList as getProductListApi,
  getProductDetail as getProductDetailApi,
  getAllProducts as getAllProductsApi
} from '~/api/product'

export const useProduct = () => {
  // 获取商品分页列表
  const getProductList = async (params: {
    pageNum?: number
    pageSize?: number
    productName?: string
    category?: string
  }) => {
    return await getProductListApi(params)
  }

  // 获取商品详情
  const getProductDetail = async (productId: number) => {
    return await getProductDetailApi(productId)
  }

  // 获取所有商品
  const getAllProducts = async () => {
    return await getAllProductsApi()
  }

  return {
    getProductList,
    getProductDetail,
    getAllProducts
  }
}
