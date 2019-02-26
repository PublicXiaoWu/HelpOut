package com.gkzxhn.helpout.presenter

import android.content.Context
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.entity.ImInfo
import com.gkzxhn.helpout.entity.OrderDispose
import com.gkzxhn.helpout.entity.VideoDuration
import com.gkzxhn.helpout.model.IOrderModel
import com.gkzxhn.helpout.model.iml.OrderModel
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.TsDialog
import com.gkzxhn.helpout.view.OrderDisposeView
import com.netease.nim.uikit.api.NimUIKit
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

/**
 * @classname：我的订单
 * @author：liushaoxiang
 * @date：2018/11/7 2:46 PM
 * @description：
 */
class OrderDisposePresenter(context: Context, view: OrderDisposeView) : BasePresenter<IOrderModel, OrderDisposeView>(context, OrderModel(), view) {

    fun getOrderDispose(page: String, mCompositeSubscription: CompositeSubscription?) {
        if (!ProjectUtils.certificationStatus()) {
            mView?.offLoadMore()
            return
        }
        mContext?.let {
            mCompositeSubscription?.add(mModel.getOrderDispose(it, page, "10")
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<OrderDispose>(it) {
                        override fun success(t: OrderDispose) {
                            mView?.offLoadMore()
                            mView?.setLastPage(t.last, t.number)
                            mView?.updateData(t.first, t.content)

                            mView?.showNullView(t.content!!.isEmpty(), "您还没有咨询订单")

                            if (t.content!!.isNotEmpty()) {
                                App.EDIT.putString("OrderId", t.content!![0].id).commit()
                            }

                        }

                        override fun onError(t: Throwable?) {
                            super.onError(t)
                            mView?.offLoadMore()
                        }
                    }))
        }
    }


    /**
     * @methodName： created by liushaoxiang on 2018/10/22 3:31 PM.
     * @description：获取网易信息
     */
    fun getImAccount(userName: String) {
        mContext?.let {
            mModel.getImAccount(it, userName)
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<ImInfo>(mContext!!) {
                        override fun success(t: ImInfo) {
                            NimUIKit.startP2PSession(mContext, t.account)
                            NimUIKit.setMsgForwardFilter { false }
                            NimUIKit.setMsgRevokeFilter { false }
                        }
                    })
        }

    }

    fun getVideoDuration(id: String, userName: String) {
        mContext?.let {
            mModel.getVideoDuration(it, id)
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<VideoDuration>(mContext!!) {
                        override fun success(t: VideoDuration) {
                            if (t.videoDuration!! <= 0) {
                                mContext?.TsDialog("视频通话时长已用完", false)
                            } else {
                                getImAccount(userName)
                            }

                        }
                    })
        }
    }

}