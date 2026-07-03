package com.example.mall.base.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 商品数据模型
 */
@Parcelize
data class Product(
    val productId: Long = 0,
    val productNo: String = "",
    val productName: String = "",
    val category: String = "",
    val brand: String = "",
    val price: Double = 0.0,
    val unit: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val status: Int = 0,
    val deleted: Int = 0,
    val createTime: String = "",
    val updateTime: String = ""
) : Parcelable

/**
 * 分页结果模型
 */
data class PageResult<T>(
    val records: List<T> = emptyList(),
    val total: Int = 0,
    val size: Int = 0,
    val current: Int = 0,
    val pages: Int = 0
)

/**
 * 通用响应结果模型
 */
data class Result<T>(
    val code: Int = 0,
    val message: String = "",
    val data: T? = null
)

/**
 * 登录请求模型
 */
data class LoginRequest(
    val phone: String,
    val password: String
)

/**
 * 注册请求模型
 */
data class RegisterRequest(
    val phone: String,
    val password: String,
    val nickname: String
)

/**
 * 登录结果模型
 */
data class LoginResult(
    val token: String = "",
    val tokenType: String = ""
)

/**
 * 客户信息模型
 */
@Parcelize
data class CustomerInfo(
    val customerId: Long = 0,
    val username: String = "",
    val phone: String = "",
    val nickname: String = "",
    val avatar: String = "",
    val gender: Int = 0,
    val email: String = "",
    val status: Int = 0,
    val createTime: String = ""
) : Parcelable

/**
 * 订单信息模型
 */
@Parcelize
data class OrderInfo(
    val orderId: Long = 0,
    val orderNo: String = "",
    val customerId: Long = 0,
    val productId: Long = 0,
    val quantity: Int = 0,
    val totalAmount: Double = 0.0,
    val status: Int = 0,
    val createTime: String = "",
    val payTime: String = "",
    val product: Product? = null
) : Parcelable

/**
 * 创建订单请求模型
 */
data class CreateOrderRequest(
    val productId: Long,
    val quantity: Int = 1
)

/**
 * 库存信息模型
 */
data class StockInfo(
    val stockId: Long = 0,
    val productId: Long = 0,
    val stockCount: Int = 0,
    val lockedCount: Int = 0,
    val updateTime: String = ""
)
