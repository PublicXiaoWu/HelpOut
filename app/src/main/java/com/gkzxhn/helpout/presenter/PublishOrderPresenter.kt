package com.gkzxhn.helpout.presenter

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.PublishOrderActivity
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.entity.PublishOrderInfo
import com.gkzxhn.helpout.entity.PublishRequestInfo
import com.gkzxhn.helpout.entity.WXLawOrderInfo
import com.gkzxhn.helpout.entity.rxbus.RxBusBean
import com.gkzxhn.helpout.model.iml.CustomerModel
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.view.PublishOrderView
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import okhttp3.ResponseBody
import org.json.JSONObject
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

class PublishOrderPresenter(context: Context, view: PublishOrderView)
    : BasePresenter<CustomerModel, PublishOrderView>(context, CustomerModel(context), view) {

    //订单id
    var orderId: String? = null


    fun init() {
    }


    fun publishOrder(category: String, reward: Double) : Subscription? {
        return mContext?.let {
            mModel.publishOrder(PublishRequestInfo(category, reward))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : HttpObserver<PublishOrderInfo>(it) {
                        override fun success(t: PublishOrderInfo) {
                            orderId = t.id
                            mView?.showPayPop(reward)
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
    fun getWxOrder(amount: Double) : Subscription? {
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
        (mView as PublishOrderActivity).popupWindow?.dismiss()
    }

    fun subscribePay(): Subscription? {
        return RxBus.instance
                .toObserverable(RxBusBean.PayStatus::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.payStatus == 1) {
                        mView?.showPaySuccess()
                    }
                }, {

                })
    }

}