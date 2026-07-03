package com.example.mall.base.network

import com.example.mall.base.data.model.Product
import com.example.mall.base.data.model.Result
import retrofit2.Call
import retrofit2.http.*

/**
 * 后端 API 服务接口定义
 * 包含所有模块的 API 定义
 */
interface ApiService {
    
    // ==================== 商品模块 ====================
    
    /**
     * 获取商品分页列表
     */
    @GET("product")
    suspend fun getProductList(
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("productName") productName: String? = null,
        @Query("category") category: String? = null
    ): Result<PageResult<Product>>
    
    /**
     * 获取商品详情
     */
    @GET("product/{productId}")
    suspend fun getProductDetail(
        @Path("productId") productId: Long
    ): Result<Product>
    
    /**
     * 获取所有商品
     */
    @GET("product/all")
    suspend fun getAllProducts(): Result<List<Product>>
    

    // ==================== 客户认证模块 ====================
    
    /**
     * 客户登录
     */
    @POST("customer/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Result<LoginResult>
    
    /**
     * 客户注册
     */
    @POST("customer/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Result<CustomerInfo>
    
    /**
     * 客户登出
     */
    @POST("customer/logout")
    suspend fun logout(
        @Body request: Map<String, String>
    ): Result<String>
    
    /**
     * 获取客户信息
     */
    @GET("customer/info")
    suspend fun getCustomerInfo(): Result<CustomerInfo>
    

    // ==================== 订单模块 ====================
    
    /**
     * 获取订单列表
     */
    @GET("order")
    suspend fun getOrderList(
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("orderNo") orderNo: String? = null
    ): Result<PageResult<OrderInfo>>
    
    /**
     * 获取订单详情
     */
    @GET("order/{orderId}")
    suspend fun getOrderDetail(
        @Path("orderId") orderId: Long
    ): Result<OrderInfo>
    
    /**
     * 创建订单
     */
    @POST("order")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): Result<OrderInfo>
    
    /**
     * 支付订单
     */
    @PUT("order/{orderId}/pay")
    suspend fun payOrder(
        @Path("orderId") orderId: Long
    ): Result<OrderInfo>
    
    /**
     * 取消订单
     */
    @PUT("order/{orderId}/cancel")
    suspend fun cancelOrder(
        @Path("orderId") orderId: Long
    ): Result<OrderInfo>
    
    /**
     * 确认收货
     */
    @PUT("order/{orderId}/receive")
    suspend fun receiveOrder(
        @Path("orderId") orderId: Long
    ): Result<OrderInfo>
    

    // ==================== 库存模块 ====================
    
    /**
     * 获取商品库存
     */
    @GET("stock/product/{productId}")
    suspend fun getStockByProductId(
        @Path("productId") productId: Long
    ): Result<StockInfo>
}
