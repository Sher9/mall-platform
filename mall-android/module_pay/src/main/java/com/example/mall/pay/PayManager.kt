package com.example.mall.pay

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.mall.base.router.RouterPath
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 支付管理器 - 单例模式
 * 封装微信支付和支付宝支付 SDK
 * 路由路径：/pay/main
 */
@Singleton
@Route(path = RouterPath.Pay.PAY)
class PayManager @Inject constructor() {

    companion object {
        private const val WECHAT_APP_ID = "your_wechat_app_id"
        private const val ALIPAY_APP_ID = "your_alipay_app_id"
    }

    private var wxApi: IWXAPI? = null

    /**
     * 初始化微信 SDK
     */
    fun initWechat(context: Context) {
        wxApi = WXAPIFactory.createWXAPI(context, WECHAT_APP_ID, true)
        wxApi?.registerApp(WECHAT_APP_ID)
    }

    /**
     * 微信支付
     * @param activity 当前 Activity
     * @param prepayId 预支付 ID (从后端获取)
     * @param callback 支付结果回调
     */
    fun wechatPay(
        activity: Activity,
        prepayId: String,
        callback: PayCallback
    ) {
        if (wxApi == null || !wxApi!!.isWXAppInstalled) {
            callback.onError("未安装微信客户端")
            return
        }

        // 这里需要后端返回微信支付所需参数
        // 参考微信支付 SDK 文档
        val request = PayReq()
        request.appId = WECHAT_APP_ID
        request.prepayId = prepayId
        // 其他参数需要从后端获取...

        val result = wxApi?.sendReq(request)
        if (result == true) {
            callback.onSuccess("正在调起微信支付")
        } else {
            callback.onError("调起微信支付失败")
        }
    }

    /**
     * 支付宝支付
     * @param activity 当前 Activity
     * @param orderInfo 支付宝订单信息 (从后端获取)
     * @param callback 支付结果回调
     */
    fun alipay(
        activity: Activity,
        orderInfo: String,
        callback: PayCallback
    ) {
        // 需要在异步线程中调用
        Thread {
            val alipay = PayTask(activity)
            val result = alipay.payV2(orderInfo, true)

            activity.runOnUiThread {
                handleAlipayResult(result, callback)
            }
        }.start()
    }

    /**
     * 处理支付宝支付结果
     */
    private fun handleAlipayResult(
        result: Map<String, String>,
        callback: PayCallback
    ) {
        val resultStatus = result["resultStatus"]
        when (resultStatus) {
            "9000" -> callback.onSuccess("支付成功")
            "8000" -> callback.onProcessing("支付处理中")
            "4000" -> callback.onError("支付失败")
            "5000" -> callback.onError("重复请求")
            "6001" -> callback.onCancel("用户取消支付")
            "6002" -> callback.onError("网络连接出错")
            "6004" -> callback.onProcessing("支付结果未知")
            else -> callback.onError("支付失败：$resultStatus")
        }
    }

    /**
     * 支付回调接口
     */
    interface PayCallback {
        fun onSuccess(message: String)
        fun onError(error: String)
        fun onCancel(message: String)
        fun onProcessing(message: String)
    }
}
