package com.example.mall.base.utils

import android.util.Log

/**
 * 日志工具类
 */
object LogUtils {
    
    private const val TAG = "MallApp"
    private var debugMode = true
    
    fun setDebugMode(debug: Boolean) {
        debugMode = debug
    }
    
    fun d(tag: String = TAG, message: String) {
        if (debugMode) Log.d(tag, message)
    }
    
    fun i(tag: String = TAG, message: String) {
        if (debugMode) Log.i(tag, message)
    }
    
    fun w(tag: String = TAG, message: String) {
        if (debugMode) Log.w(tag, message)
    }
    
    fun e(tag: String = TAG, message: String) {
        if (debugMode) Log.e(tag, message)
    }
    
    fun e(tag: String = TAG, message: String, tr: Throwable) {
        if (debugMode) Log.e(tag, message, tr)
    }
}
