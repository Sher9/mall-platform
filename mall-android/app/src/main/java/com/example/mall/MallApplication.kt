package com.example.mall

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.example.mall.base.di.BaseApp
import dagger.hilt.android.HiltAndroidApp

/**
 * 应用 Application 类
 * 负责初始化全局配置：Hilt、ARouter、MMKV等
 */
@HiltAndroidApp
class MallApplication : BaseApp() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化 ARouter
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
        
        // 初始化日志工具
        LogUtils.setDebugMode(BuildConfig.DEBUG)
    }
    
    override fun onTerminate() {
        super.onTerminate()
        ARouter.destroy()
    }
}
