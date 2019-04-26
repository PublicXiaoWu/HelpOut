package com.gkzxhn.helpout.entity.rxbus

/**
 * @classname：dd
 * @author：liushaoxiang
 * @date：2018/11/8 2:23 PM
 * @description：
 */
class RxBusBean {

    var id: String? = null

    class HomePoint(var show: Boolean)
    /****** 添加好友的通知 ******/
    class AddPoint(var show: Boolean)

    /****** 我的生活圈有新的消息通知 ******/
    class MyLivesCirclePoint(var show: Boolean)

    /****** 添加好友验证通过 ******/
    class AddFriendPass(var userName: String, var account: String)

    /****** 已经加载过生活圈了 通知发现页面刷新新的未读信息 ******/
    class ChangeFindUnRead()

    class HomeTopRedPoint(var show: Boolean)
    class LoginOut(var show: Boolean)
    /****** 刷新订单数据 ******/
    class RefreshOrder(var show: Boolean)

    /****** 刷新抢单数据 ******/
    class RefreshGrabOrder(var show: Boolean)

    //code 0 表示成功
    class PublishEntity(val code: Int)

    /**
     * payStatus  1 表示支付成功    0 表示失败  2 表示取消
     * payType  1 支付宝    2 微信
     */
    class PayStatus(var payStatus: Int?=null, var payType: Int?=null)

}