package com.gkzxhn.helpout.view

import com.gkzxhn.helpout.entity.LivesCircle
import com.gkzxhn.helpout.entity.LivesCircleDetails

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/22 3:35 PM
 * @description：生活圈
 */

interface LivesCircleView : BaseView {
    fun finishActivity()

    fun offLoadMore()

    fun updateData(data: List<LivesCircle.ContentBean>,isFirst:Boolean)

    fun setLastPage(lastPage: Boolean, page: Int)

    fun praiseSuccess(position: Int)

    fun loadLivesCircleDetailsUI(t: LivesCircleDetails)
    fun endLoadMore()
    fun finishRefresh()
}