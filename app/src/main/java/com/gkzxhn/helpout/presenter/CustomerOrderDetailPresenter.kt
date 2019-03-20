package com.gkzxhn.helpout.presenter

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.CustomerOrderDetailActivity
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.entity.CommentInfo
import com.gkzxhn.helpout.entity.CustomerOrderDetailInfo
import com.gkzxhn.helpout.entity.ImInfo
import com.gkzxhn.helpout.entity.WXLawOrderInfo
import com.gkzxhn.helpout.entity.rxbus.PayStatus
import com.gkzxhn.helpout.model.iml.CustomerModel
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.view.CustomerOrderDetailView
import com.netease.nim.uikit.api.NimUIKit
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import okhttp3.ResponseBody
import org.json.JSONObject
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

class CustomerOrderDetailPresenter(context: Context, view: CustomerOrderDetailView)
    : BasePresenter<CustomerModel, CustomerOrderDetailView>(context, CustomerModel(context), view) {

    //订单id
    var orderId: String? = null

    fun getCustomerOrderDetail(orderId: String) : Subscription?{
        this.orderId = orderId
        return mContext?.let {
            mModel.getCustomerOrderDetail(orderId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : HttpObserver<CustomerOrderDetailInfo>(it) {
                        override fun success(t: CustomerOrderDetailInfo) {
                            mView?.setData(t)
                        }
                    })
        }
    }

    /**
     * 获取律师云信账号然后通话
     */
    fun getImInfo(username: String) : Subscription?{
        return mContext?.let {
            mModel.getImAccount(it, username)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : HttpObserver<ImInfo>(it) {
                        override fun success(t: ImInfo) {
                            NimUIKit.startP2PSession(it, t.account)
                        }
                    })
        }
    }

    /**
     * 删除订单
     */
    fun deleteOrder(orderId: String) : Subscription?{
        return  mContext?.let {
            mModel.deleteOrder(orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : HttpObserver<ResponseBody?>(it) {
                    override fun success(t: ResponseBody?) {
                        it.showToast(it.getString(R.string.delete_success))
                        mView?.close()
                    }
                })
        }
    }

    /**
     * 取消订单
     */
    fun cancelOrder(orderId: String) : Subscription?{
        return mContext?.let {
            mModel.cancelOrder(orderId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : HttpObserver<ResponseBody?>(it) {
                        override fun success(t: ResponseBody?) {
                            getCustomerOrderDetail(orderId)
                        }
                    })
        }
    }

    /**
     * 结束咨询
     */
    fun endOrder(orderId: String, comment:String?, rate: Int, isResolved: Boolean): Subscription? {
        return mContext?.let {
            mModel.applyOrderComment(CommentInfo(rate, comment, isResolved, orderId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : HttpObserver<ResponseBody>(it) {
                    override fun success(t: ResponseBody) {
                        getCustomerOrderDetail(orderId)
                    }
                })
        }
    }

    /**
     * 模拟通话
     */
    fun mockVideoChart(orderId: String): Subscription? {
        return mContext?.let {
            it.showToast("模拟通话...")
            mModel.mockVideoChart(orderId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : HttpObserver<ResponseBody?>(it) {
                        override fun success(t: ResponseBody?) {
                            getCustomerOrderDetail(orderId)
                            it.showToast("模拟通话完成...")
                        }
                    })
        }
    }

    /**
     * 获取订单评价
     */
    fun getComments(orderId: String): Subscription? {
        return mContext?.let {
            mModel.getComment(orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : HttpObserver<CommentInfo>(it) {
                    override fun success(t: CommentInfo) {
                        mView?.setCommentsData(t)
                    }

                })
        }
    }

    /**
     * 获取支付宝订单号
     */
    fun getAliOrder() : Subscription?{
        if (TextUtils.isEmpty(orderId)) {
            mContext?.showToast("订单号错误,请重新下单")
            return null
        }
        return mContext?.let {
            mModel.getAliOrder(orderId!!)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : HttpObserver<ResponseBody>(it) {
                        override fun success(t: ResponseBody) {
                            val body = try {
                                JSONObject(t.string()).getString("body")
                            } catch (e: Exception) {
                                null
                            }
                            body?.let { it1 -> mView?.alipay(it1) }
                        }
                    })
        }
    }

    private var amount = 0.0
    fun getWxOrder(amount: Double) : Subscription?{
        this.amount = amount
        if (TextUtils.isEmpty(orderId)) {
            mContext?.showToast("订单号错误,请重新下单")
            return null
        }
        return mContext?.let {
            mModel.getWxOrder(orderId!!)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : HttpObserver<WXLawOrderInfo>(it) {
                        override fun success(t: WXLawOrderInfo) {
                            val request = PayReq()
                            request.appId = t.appId
                            request.partnerId = t.merchantId
                            request.prepayId = t.prepayId
                            request.packageValue = t.extension
                            request.nonceStr = t.nonce
                            request.timeStamp = t.timestamp
                            request.sign = t.sign
                            wxpay(request)
                            Log.d(this.javaClass.simpleName, t.toString())
                        }
                    })
        }
    }

    /**
     * 微信支付
     */
    fun wxpay(request: PayReq) {
        val api: IWXAPI = WXAPIFactory.createWXAPI(mContext, request.appId, true)
        if (!api.isWXAppInstalled) {
            //微信未安装
            mContext?.let{ it.showToast(it.getString(R.string.weixin_uninstalled)) }
            return
        }
        /*if (!api.isWXAppSupportAPI) {
            context.toast("Android版本太低,不支持微信支付")
            return
        }*/
        api.registerApp(request.appId)

        /*val signParams = LinkedList<Pair<String, String>>()
        signParams.add(Pair("appid", request.appId))
        signParams.add(Pair("noncestr", request.nonceStr))
        signParams.add(Pair("package", request.packageValue))
        signParams.add(Pair("partnerid", request.partnerId))
        signParams.add(Pair("prepayid", request.prepayId))
        //signParams.add(new BasicNameValuePair("sign", sign));
        signParams.add(Pair("timestamp", request.timeStamp))
        request.sign = getAppSign(signParams)*/
        request.extData = "$amount&"
        api.sendReq(request)
        (mView as CustomerOrderDetailActivity).popupWindow?.dismiss()
    }

    fun subscribePay(): Subscription? {
        return RxBus.instance
                .toObserverable(PayStatus::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.payStatus == 1) {
                        mView?.showPaySuccess()
                    }
                }, {

                })
    }

}