package com.gkzxhn.helpout.model.iml

import android.content.Context
import com.gkzxhn.helpout.entity.PublishOrderInfo
import com.gkzxhn.helpout.entity.PublishRequestInfo
import com.gkzxhn.helpout.extensions.getRequestBody
import com.gkzxhn.helpout.net.RetrofitClient
import com.google.gson.Gson
import rx.Observable
import rx.schedulers.Schedulers

class CustomerModel (val context: Context) : BaseModel() {

    fun publishOrder(publishRequestInfo: PublishRequestInfo): Observable<PublishOrderInfo> {
        return RetrofitClient.getInstance(context)
                .mApi
                .publishOrder(Gson().getRequestBody(publishRequestInfo))
                .subscribeOn(Schedulers.io())
    }
}