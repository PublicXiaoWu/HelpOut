package com.gkzxhn.helpout.presenter

import android.content.Context
import com.gkzxhn.helpout.entity.PublishOrderInfo
import com.gkzxhn.helpout.entity.PublishRequestInfo
import com.gkzxhn.helpout.model.iml.CustomerModel
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.view.PublishOrderView
import rx.android.schedulers.AndroidSchedulers

class PublishOrderPresenter(context: Context, view: PublishOrderView)
    : BasePresenter<CustomerModel, PublishOrderView>(context, CustomerModel(context), view) {

    fun publishOrder(category: String, reward: Int) {
        mContext?.let {
            mModel.publishOrder(PublishRequestInfo(category, reward))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : HttpObserver<PublishOrderInfo>(it){
                    override fun success(t: PublishOrderInfo) {
                        mView?.showPayPop(reward)
                    }
                })
        }
    }
}