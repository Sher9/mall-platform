package com.example.mall.base.router

/**
 * ARouter 路由路径常量定义
 * 所有模块的路由路径统一在此管理
 */
object RouterPath {
    
    // 用户模块
    object User {
        const val LOGIN = "/user/login"
        const val REGISTER = "/user/register"
        const val CENTER = "/user/center"
        const val ADDRESS = "/user/address"
    }
    
    // 首页模块
    object Home {
        const val HOME = "/home/main"
        const val SEARCH = "/home/search"
        const val CATEGORY = "/home/category"
    }
    
    // 商品模块
    object Product {
        const val LIST = "/product/list"
        const val DETAIL = "/product/detail"
        const val SKU = "/product/sku"
    }
    
    // 购物车模块
    object Cart {
        const val CART = "/cart/main"
    }
    
    // 订单模块
    object Order {
        const val LIST = "/order/list"
        const val DETAIL = "/order/detail"
        const val CREATE = "/order/create"
        const val PAY = "/order/pay"
    }
    
    // 支付模块
    object Pay {
        const val PAY = "/pay/main"
    }
}
