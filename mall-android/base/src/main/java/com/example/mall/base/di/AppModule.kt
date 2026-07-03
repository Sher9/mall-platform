package com.example.mall.base.di

import android.content.Context
import androidx.room.Room
import com.example.mall.base.data.local.AppDatabase
import com.example.mall.base.data.repository.ProductRepository
import com.example.mall.base.network.ApiService
import com.example.mall.base.network.RetrofitManager
import com.tencent.mmkv.MMKV
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt 依赖注入模块
 * 提供全局单例对象
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * 提供 MMKV 实例
     */
    @Provides
    @Singleton
    fun provideMMKV(@ApplicationContext context: Context): MMKV {
        MMKV.initialize(context)
        return MMKV.defaultMMKV()
    }
    
    /**
     * 提供 Room 数据库实例
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mall_database"
        ).build()
    }
    
    /**
     * 提供 Retrofit ApiService 实例
     */
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitManager.createService(ApiService::class.java)
    }
    
    /**
     * 提供商品数据仓库实例
     */
    @Provides
    @Singleton
    fun provideProductRepository(): ProductRepository {
        return ProductRepository()
    }
}
