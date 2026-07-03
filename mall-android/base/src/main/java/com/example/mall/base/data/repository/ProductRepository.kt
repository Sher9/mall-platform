package com.example.mall.base.data.repository

import com.example.mall.base.data.model.PageResult
import com.example.mall.base.data.model.Product
import com.example.mall.base.data.model.Result
import com.example.mall.base.network.ApiService
import com.example.mall.base.network.RetrofitManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 商品数据仓库
 * 屏蔽网络与本地差异，提供统一的数据访问接口
 */
class ProductRepository {
    
    private val apiService = RetrofitManager.createService(ApiService::class.java)
    
    /**
     * 获取商品分页列表
     */
    suspend fun getProductList(
        pageNum: Int = 1,
        pageSize: Int = 20,
        productName: String? = null,
        category: String? = null
    ): Result<PageResult<Product>> {
        return try {
            apiService.getProductList(pageNum, pageSize, productName, category)
        } catch (e: Exception) {
            Result(500, "网络请求失败: ${e.message}", null)
        }
    }
    
    /**
     * 获取商品详情
     */
    suspend fun getProductDetail(productId: Long): Result<Product> {
        return try {
            apiService.getProductDetail(productId)
        } catch (e: Exception) {
            Result(500, "网络请求失败: ${e.message}", null)
        }
    }
    
    /**
     * 获取所有商品 (Flow 方式)
     */
    fun getAllProductsFlow(): Flow<Result<List<Product>>> = flow {
        try {
            val result = apiService.getAllProducts()
            emit(result)
        } catch (e: Exception) {
            emit(Result(500, "网络请求失败: ${e.message}", null))
        }
    }
}
