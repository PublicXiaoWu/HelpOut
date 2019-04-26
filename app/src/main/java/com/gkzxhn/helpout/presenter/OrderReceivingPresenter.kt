package com.gkzxhn.helpout.presenter

import android.content.Context
import android.content.Intent
import android.widget.TextView
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.OrderActivity
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.entity.OrderMyInfo
import com.gkzxhn.helpout.entity.OrderReceiving
import com.gkzxhn.helpout.entity.rxbus.RxBusBean
import com.gkzxhn.helpout.model.IOrderModel
import com.gkzxhn.helpout.model.iml.OrderModel
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.utils.selectDialog
import com.gkzxhn.helpout.view.OrderReceivingView
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

/**
 * @classname：抢单
 * @author：liushaoxiang
 * @date：2018/11/7 2:46 PM
 * @description：
 */
class OrderReceivingPresenter(context: Context, view: OrderReceivingView) : BasePresenter<IOrderModel, OrderReceivingView>(context, OrderModel(), view) {

    fun getOrderReceiving(page: String, mCompositeSubscription: CompositeSubscription?) {
        mContext?.let {
            mCompositeSubscription?.add(mModel.getOrderReceiving(it, page, "10","createdTime,desc")
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<OrderReceiving>(it) {
                        override fun success(t: OrderReceiving) {
                            mView?.offLoadMore()
                            mView?.setLastPage(t.last, t.number)
                            mView?.showNullView(t.content!!.isEmpty())
                            mView?.updateData(t.first, t.content)
                        }

                        override fun onError(t: Throwable?) {
                            super.onError(t)
                            mView?.offLoadMore()
                        }
                    }))

        }
    }

    fun acceptRushOrder(id: String, mCompositeSubscription: CompositeSubscription?) {
        mContext?.let {
            mCompositeSubscription?.add(mModel.acceptRushOrder(it, id)
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<OrderMyInfo>(it) {
                        override fun success(t: OrderMyInfo) {
                            if (t.status == Constants.ORDER_STATE_ACCEPTED) {
                                RxBus.instance.post(RxBusBean.HomePoint(true))
                                val selectDialog = it.selectDialog("恭喜您，抢单成功", false)
                                val cancel = selectDialog.findViewById<TextView>(R.id.dialog_cancel)
                                val next = selectDialog.findViewById<TextView>(R.id.dialog_save)
                                cancel.text = "关闭"
                                next.text = "查看"
                                next.setOnClickListener {
                                    val intent = Intent(selectDialog.context, OrderActivity::class.java)
                                    intent.putExtra("orderId", id)
                                    intent.putExtra("orderState", 2)
                                    selectDialog.context.startActivity(intent)
                                    selectDialog.dismiss()
                                }
                                getOrderReceiving("0",mCompositeSubscription)

                            }
                        }
                    }))
        }
    }

}
