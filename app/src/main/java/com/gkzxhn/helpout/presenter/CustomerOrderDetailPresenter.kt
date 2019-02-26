package com.gkzxhn.helpout.presenter

import android.content.Context
import com.gkzxhn.helpout.entity.CustomerOrderDetailInfo
import com.gkzxhn.helpout.model.iml.CustomerModel
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.view.CustomerOrderDetailView
import rx.android.schedulers.AndroidSchedulers

class CustomerOrderDetailPresenter(context: Context, view: CustomerOrderDetailView)
    : BasePresenter<CustomerModel, CustomerOrderDetailView>(context, CustomerModel(context), view) {

    //订单id
    var orderId: String? = null

    fun getCustomerOrderDetail(orderId: String) {
        mContext?.let {
            mModel.getCustomerOrderDetail(orderId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : HttpObserver<CustomerOrderDetailInfo>(it) {
                        override fun success(t: CustomerOrderDetailInfo) {
                            mView?.setData(t)
                        }
                    })
        }
    }
}