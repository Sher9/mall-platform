package com.example.mall.base.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 网络请求管理类 - 单例模式
 * 封装 Retrofit + OkHttp
 */
object RetrofitManager {
    
    // 后端服务 Base URL (通过 Gateway 访问)
    private const val BASE_URL = "http://localhost:8000/api/"
    
    // OkHttpClient 超时配置
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L
    
    // 日志拦截器
    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    // OkHttpClient 配置
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // 添加 token 拦截器
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                
                // 从本地获取 token (MMKV 存储)
                val token = com.tencent.mmkv.MMKV.defaultMMKV()?.getString("token", "")
                if (!token.isNullOrEmpty()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }
                
                requestBuilder.method(original.method, original.body)
                chain.proceed(requestBuilder.build())
            }
            .build()
    }
    
    // Retrofit 实例
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * 创建 API 服务
     */
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}
