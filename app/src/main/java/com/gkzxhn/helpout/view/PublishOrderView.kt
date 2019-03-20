package com.gkzxhn.helpout.view

interface PublishOrderView : BaseView {
    fun showPayPop(reward: Double)
    fun alipay(orderInfo: String)
    fun showPaySuccess()
}