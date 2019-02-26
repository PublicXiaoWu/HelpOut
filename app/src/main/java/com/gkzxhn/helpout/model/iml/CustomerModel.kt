package com.gkzxhn.helpout.model.iml

import android.content.Context
import com.gkzxhn.helpout.entity.CustomerOrderDetailInfo
import com.gkzxhn.helpout.entity.PublishOrderInfo
import com.gkzxhn.helpout.entity.PublishRequestInfo
import com.gkzxhn.helpout.extensions.getRequestBody
import com.gkzxhn.helpout.net.RetrofitClient
import com.google.gson.Gson
import okhttp3.ResponseBody
import rx.Observable
import rx.schedulers.Schedulers

class CustomerModel (val context: Context) : BaseModel() {

    /**
     * 发布抢单
     */
    fun publishOrder(publishRequestInfo: PublishRequestInfo): Observable<PublishOrderInfo> {
        return RetrofitClient.getInstance(context)
                .mApi
                .publishOrder(Gson().getRequestBody(publishRequestInfo))
                .subscribeOn(Schedulers.io())
    }

    /**
     * 获取支付宝订单
     */
    fun getAliOrder(id: String) :Observable<ResponseBody>{
        return RetrofitClient.getInstance(context)
                .mApi
                .getAliOrder(id)
                .subscribeOn(Schedulers.io())
    }

    /**
     * 获取支付宝订单
     */
    fun getCustomerOrderDetail(id: String) :Observable<CustomerOrderDetailInfo>{
        return RetrofitClient.getInstance(context)
                .mApi
                .getCustomerOrderDetail(id)
                .subscribeOn(Schedulers.io())
    }
}