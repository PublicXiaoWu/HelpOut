package com.gkzxhn.helpout.presenter

import android.content.Context
import com.gkzxhn.helpout.entity.LivesCircle
import com.gkzxhn.helpout.model.ILivesCircleModel
import com.gkzxhn.helpout.model.iml.LivesCircleModel
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.view.LivesCircleView
import rx.android.schedulers.AndroidSchedulers

/**
 * @classname：生活圈
 * @author：liushaoxiang
 * @date：2019/4/22 3:34 PM
 * @description：
 */

class LivesCirclePresenter(context: Context, view: LivesCircleView) : BasePresenter<ILivesCircleModel, LivesCircleView>(context, LivesCircleModel(), view) {

    fun getLivesCircle() {
        mContext?.let {
            mModel.getLivesCircle(it,"0","10")
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<LivesCircle>(mContext!!) {
                        override fun success(t: LivesCircle) {

                        }
                    })
        }
    }



}