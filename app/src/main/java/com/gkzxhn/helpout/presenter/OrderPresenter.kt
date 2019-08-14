package com.gkzxhn.helpout.presenter

import android.content.Context
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.entity.ImInfo
import com.gkzxhn.helpout.entity.OrderComment
import com.gkzxhn.helpout.entity.OrderMyInfo
import com.gkzxhn.helpout.entity.OrderRushInfo
import com.gkzxhn.helpout.entity.UIInfo.LawChannel
import com.gkzxhn.helpout.entity.rxbus.RxBusBean
import com.gkzxhn.helpout.model.IOrderModel
import com.gkzxhn.helpout.model.iml.OrderModel
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.utils.StringUtils
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.view.OrderView
import com.netease.nim.uikit.api.NimUIKit
import okhttp3.ResponseBody
import rx.android.schedulers.AndroidSchedulers

/**
 * @classname：接单详情
 * @author：liushaoxiang
 * @date：2018/11/7 2:46 PM
 * @description：
 */
class OrderPresenter(context: Context, view: OrderView) : BasePresenter<IOrderModel, OrderView>(context, OrderModel(), view) {

    /****** 客户的网易ID ******/
    var userName = ""
    var videoDuration = 3.0
    var orderId = ""

    /****** 1，获取 抢单的明细 ******/
    fun getOrderRushInfo(id: String) {
        mContext?.let {
            mModel.getOrderRushInfo(it, id)
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<OrderRushInfo>(it) {
                        override fun success(t: OrderRushInfo) {
                            mView?.setName(t.customer?.name!!)
                            mView?.setReward("￥" + t.reward)
                            mView?.setTime(StringUtils.parseDate(t.createdTime))
                            mView?.setOrderNumber(t.number.toString())
                            mView?.setNextText(View.VISIBLE, "抢单")
                            mView?.setOrderImage(t.customer?.username)
                            mView?.setOrderState("已支付")

                            mView?.setShowOrderState(View.GONE, "", "", "")
                            mView?.setShowGetMoney(View.GONE, "", "")
                            mView?.setShowEvaluation(View.GONE, "", "", 0)
                            val str1 = LawChannel.find(t.category)!!.name
                            mView?.setOrderType(str1)
                        }
                    })
        }
    }

    /****** 2，获取 指定订单的明细 ******/
    fun getOrderMyInfo(id: String) {
        orderId = id
        mContext?.let {
            mModel.getOrderMyInfo(it, id)
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<OrderMyInfo>(it) {
                        override fun success(t: OrderMyInfo) {
                            initOrderInfo(t)

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
            mModel.mockVideoChart(it, orderId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : HttpObserver<ResponseBody?>(it) {
                        override fun success(t: ResponseBody?) {
                            getOrderMyInfo(orderId)
                            it.showToast("模拟通话完成...")
                        }
                    })
        }
    }

    private fun initOrderInfo(t: OrderMyInfo) {
        mView?.setName(t.customer?.name!!)
        mView?.setReward("￥" + t.reward)
        mView?.setOrderState("已支付")
        mView?.setTime(StringUtils.parseDate(t.createdTime))
        mView?.setOrderNumber(t.number.toString())
        mView?.setOrderImage(t.customer?.username)

        userName = t.customer?.username!!
        videoDuration = t.videoDuration

        val str1 = LawChannel.find(t.category!!)?.name
        if (str1 != null) {
            mView?.setOrderType(str1)
        }
        when (t.status) {
        /****** 待接单 ******/
            Constants.ORDER_STATE_PENDING_RECEIVING -> {
                mView?.setOrderState("待接单")
                mView?.setShowOrderState(View.GONE, "", "", "")
            }
        /****** 已接单 ******/
            Constants.ORDER_STATE_ACCEPTED -> {
                mView?.setShowOrderState(View.VISIBLE, "已接单",
                        "接单时间：" + StringUtils.parseDate(t.acceptedTime),
                        "")
                mView?.setNextText(View.VISIBLE, App.mContext.resources.getString(R.string.send_message))
                mView?.setShowGetMoney(View.GONE, "", "")
            }
        /****** 处理中 ******/
            Constants.ORDER_STATE_PROCESSING -> {
                mView?.setShowOrderState(View.VISIBLE, "处理中",
                        "接单时间：" + StringUtils.parseDate(t.acceptedTime),
                        "")
                mView?.setShowGetMoney(View.VISIBLE, "处理中", "＊三小时内无再次咨询，系统将自动结束订单；")
                mView?.setShowEvaluation(View.GONE, "", "", 0)

                mView?.setNextText(View.VISIBLE, App.mContext.resources.getString(R.string.send_message))
                mView?.setOrderStateNameColor(App.mContext.resources.getColor(R.color.order_red))
            }
        /****** 已完成 ******/
            Constants.ORDER_STATE_COMPLETE -> {
                mView?.setShowOrderState(View.VISIBLE, "已完成",
                        "接单时间：" + StringUtils.parseDate(t.acceptedTime),
                        "完成时间：" + StringUtils.parseDate(t.endTime))
                mView?.setShowGetMoney(View.GONE, "赏金到账", "")
                mView?.setNextText(View.VISIBLE, App.mContext.resources.getString(R.string.delete_lawyer_order))
                getOrderComment(t.id!!)
            }
        /******  已关闭 ******/
            Constants.ORDER_STATE_REFUSED -> {
                mView?.setShowOrderState(View.VISIBLE, "已关闭",
                        "接单时间：" + StringUtils.parseDate(t.acceptedTime),
                        "完成时间：" + StringUtils.parseDate(t.endTime))
                mView?.setOrderStateColor(App.mContext.resources.getColor(R.color.order_red))
                mView?.setShowGetMoney(View.GONE, "赏金到账", "")
                mView?.setNextText(View.VISIBLE, App.mContext.resources.getString(R.string.delete_lawyer_order))

                getOrderComment(t.id!!)
            }
        /******  已取消 ******/
            Constants.ORDER_STATE_CANCELLED -> {
                mView?.setOrderState("已取消")
                mView?.setOrderStateColor(App.mContext.resources.getColor(R.color.order_red))
                mView?.setShowOrderState(View.GONE, "", "", "")
                mView?.setShowGetMoney(View.GONE, "", "")
                mView?.setShowEvaluation(View.GONE, "", "", 0)
            }
        /******  待付款 ******/
            Constants.ORDER_STATE_PENDING_PAYMENT -> {
                mView?.setOrderState("待付款")
                mView?.setOrderStateColor(App.mContext.resources.getColor(R.color.order_red))
                mView?.setShowOrderState(View.GONE, "", "", "")
                mView?.setShowGetMoney(View.GONE, "", "")

                mView?.setShowEvaluation(View.GONE, "", "", 0)

            }
        /******  待审核 ******/
            Constants.ORDER_STATE_PENDING_APPROVAL -> {
                mView?.setOrderState("待审核")
                mView?.setOrderStateColor(App.mContext.resources.getColor(R.color.order_red))
                mView?.setShowOrderState(View.GONE, "", "", "")
                mView?.setShowGetMoney(View.GONE, "", "")
                mView?.setShowEvaluation(View.GONE, "", "", 0)

            }
        }
    }

    /****** 抢单 ******/
    fun acceptRushOrder(id: String) {
        mContext?.let {
            mModel.acceptRushOrder(it, id)
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<OrderMyInfo>(it) {
                        override fun success(t: OrderMyInfo) {
                            if (t.status == Constants.ORDER_STATE_ACCEPTED) {
                                mContext?.showToast("抢单成功")
                                RxBus.instance.post(RxBusBean.HomePoint(true))
                                /****** 改变订单的状态为已接单 ******/
                                mView?.setOrderState(2)
                                initOrderInfo(t)
                            } else {
                                mContext?.showToast("抢单失败")
                            }
                        }
                    })
        }
    }

    private fun getOrderComment(id: String) {
        mContext?.let {
            mModel.getOrderComment(it, id)
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<OrderComment>(it) {
                        override fun success(t: OrderComment) {
                            val isResolved = if (t.isResolved!!) "已解决问题" else "未解决问题"
                            var evaluation = if (t.content == null || t.content!!.isEmpty()) {
                                "服务评价：此用户没有填写评价"
                            } else {
                                "服务评价：" + t.content
                            }
                            mView?.setShowEvaluation(View.VISIBLE, isResolved, evaluation, t.rate!!)
                        }
                    })
        }
    }

    fun sendMessage() {
        mockVideoChart(orderId)
//        if (videoDuration > 0) {
//            getImAccount(userName)
//        } else {
//            mContext?.TsDialog("视频通话时长已用完", false)
//        }
    }

    fun deleteLawyerOrder(id: String) {
        mContext?.let {
            mModel.deleteOrder(it, id)
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<ResponseBody?>(it) {
                        override fun success(t: ResponseBody?) {
                            it.showToast(it.getString(R.string.delete_success))
                            mView?.onFinish()
                        }
                    })
        }
    }

    /**
     * @methodName： created by liushaoxiang on 2018/10/22 3:31 PM.
     * @description：获取网易信息
     */
    private fun getImAccount(userName: String) {
        mContext?.let {
            mModel.getImAccount(it, userName)
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<ImInfo>(mContext!!) {
                        override fun success(t: ImInfo) {
                            // 删除与某个聊天对象的全部消息记录
//                            NIMClient.getService(MsgService::class.java).clearChattingHistory(t.account, SessionTypeEnum.P2P)
                            NimUIKit.startP2PSession(mContext, t.account)
                        }
                    })
        }
    }

}