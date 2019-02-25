package com.gkzxhn.helpout.presenter

import android.content.Context
import android.text.TextUtils
import com.gkzxhn.helpout.entity.PublishOrderInfo
import com.gkzxhn.helpout.entity.PublishRequestInfo
import com.gkzxhn.helpout.model.iml.CustomerModel
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.view.PublishOrderView
import okhttp3.ResponseBody
import org.json.JSONObject
import rx.android.schedulers.AndroidSchedulers

class PublishOrderPresenter(context: Context, view: PublishOrderView)
    : BasePresenter<CustomerModel, PublishOrderView>(context, CustomerModel(context), view) {

    //订单id
    var orderId: String? = null

    fun publishOrder(category: String, reward: Double) {
        mContext?.let {
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
    fun getAliOrder() {
        if (TextUtils.isEmpty(orderId)) {
            mContext?.showToast("订单号错误,请重新下单")
            return
        }
        mContext?.let {
            mModel.getAliOrder(orderId!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : HttpObserver<ResponseBody>(it){
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
}