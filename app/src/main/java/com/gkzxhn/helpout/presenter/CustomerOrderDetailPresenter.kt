package com.gkzxhn.helpout.presenter

import android.content.Context
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.entity.CommentInfo
import com.gkzxhn.helpout.entity.CustomerOrderDetailInfo
import com.gkzxhn.helpout.entity.ImInfo
import com.gkzxhn.helpout.model.iml.CustomerModel
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.view.CustomerOrderDetailView
import com.netease.nim.uikit.api.NimUIKit
import okhttp3.ResponseBody
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

    /**
     * 获取律师云信账号然后通话
     */
    fun getImInfo(username: String) {
        mContext?.let {
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
    fun deleteOrder(orderId: String) {
        mContext?.let {
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
    fun cancelOrder(orderId: String) {
        mContext?.let {
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
    fun endOrder(orderId: String, comment:String?, rate: Int, isResolved: Boolean) {
        mContext?.let {
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
    fun mockVideoChart(orderId: String) {
        mContext?.let {
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
    fun getComments(orderId: String) {
        mContext?.let {
            mModel.getComment(orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : HttpObserver<CommentInfo>(it) {
                    override fun success(t: CommentInfo) {
                        mView?.setCommentsData(t)
                    }

                })
        }
    }
}