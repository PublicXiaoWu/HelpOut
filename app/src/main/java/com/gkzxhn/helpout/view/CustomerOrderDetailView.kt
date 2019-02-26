package com.gkzxhn.helpout.view

import com.gkzxhn.helpout.entity.CustomerOrderDetailInfo

interface CustomerOrderDetailView : BaseView {

    fun setData(customerOrderDetailInfo: CustomerOrderDetailInfo)
}