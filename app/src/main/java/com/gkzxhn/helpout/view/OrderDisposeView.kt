package com.gkzxhn.helpout.view

import com.gkzxhn.helpout.entity.OrderDispose

/**
 * Explanation:
 * @author LSX
 *    -----2018/9/6
 */

interface OrderDisposeView : BaseView {

    fun offLoadMore()

    fun updateData(clear: Boolean,data: List<OrderDispose.ContentBean>?)

    fun setLastPage(lastPage: Boolean,page:Int)

    fun showNullView(show:Boolean,string: String)

}