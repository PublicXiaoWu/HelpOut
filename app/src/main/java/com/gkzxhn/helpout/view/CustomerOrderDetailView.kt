package com.gkzxhn.helpout.view

import com.gkzxhn.helpout.entity.CommentInfo
import com.gkzxhn.helpout.entity.CustomerOrderDetailInfo

interface CustomerOrderDetailView : BaseView {

    fun setData(customerOrderDetailInfo: CustomerOrderDetailInfo)
    fun setCommentsData(commentInfo: CommentInfo)
    fun close()
    fun alipay(orderInfo: String)
    fun showPayPop(reward: Double)
    fun showPaySuccess()
}