package com.gkzxhn.helpout.entity

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

    /****** 添加好友验证通过 ******/
    class AddFriendPass(var userName: String,var account:String)

    class HomeTopRedPoint(var show: Boolean)
    class LoginOut(var show: Boolean)
    /****** 刷新订单数据 ******/
    class RefreshOrder(var show: Boolean)

    /****** 刷新抢单数据 ******/
    class RefreshGrabOrder(var show: Boolean)

}