# 项目混淆规则

# 基础混淆规则
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 保留注解
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation { *; }

# 保留泛型
-keepattributes Signature

# 保留抛出异常
-keepattributes Exceptions

# Android 核心代码
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

# 保留 support 下的所有类及其内部类
-keep class android.support.** { *; }
-keep interface android.support.** { *; }

# 保留 Jetpack 组件
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# Hilt 依赖注入
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class javax.annotation.** { *; }

# ARouter 路由
-keep public class com.alibaba.android.arouter.routes.** { *; }
-keep public class com.alibaba.android.arouter.facade.** { *; }
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe { *; }

# Retrofit
-keep class retrofit2.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# Gson
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }

# Room 数据库
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity { *; }

# Glide 图片加载
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# 微信支付 SDK
-keep class com.tencent.mm.opensdk.** { *; }

# 支付宝 SDK
-keep class com.alipay.sdk.** { *; }
-keep class com.alipay.android.app.** { *; }

# Kotlin 协程
-keepclassmembers class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keepclassmembers class kotlinx.coroutines.android.AndroidExceptionPreHandler { *; }
-keepclassmembers class kotlinx.coroutines.android.AndroidDispatcherFactory { *; }

# 保留 ViewBinding
-keep class * implements androidx.viewbinding.ViewBinding { *; }

# 保留 Parcelize 数据类
-keep class * implements android.os.Parcelable { *; }
-keep class * implements android.os.Parcelable$Creator { *; }
