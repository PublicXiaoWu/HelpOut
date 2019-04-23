package com.gkzxhn.helpout.presenter

import android.content.Context
import com.gkzxhn.helpout.entity.LivesCircle
import com.gkzxhn.helpout.model.ILivesCircleModel
import com.gkzxhn.helpout.model.iml.LivesCircleModel
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.HttpObserverNoDialog
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.view.LivesCircleView
import okhttp3.ResponseBody
import rx.android.schedulers.AndroidSchedulers

/**
 * @classname：生活圈
 * @author：liushaoxiang
 * @date：2019/4/22 3:34 PM
 * @description：
 */

class LivesCirclePresenter(context: Context, view: LivesCircleView) : BasePresenter<ILivesCircleModel, LivesCircleView>(context, LivesCircleModel(), view) {

    fun getLivesCircle(page: String, size: String) {
        mContext?.let {
            mModel.getLivesCircle(it, "0", "100")
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<LivesCircle>(mContext!!) {
                        override fun success(t: LivesCircle) {
                            mView?.updateData(t.content!!)
                            mView?.setLastPage(t.isLast, 0)
                        }
                    })
        }
    }


    fun getMyLivesCircle(page: String, size: String) {
        mContext?.let {
            mModel.getMyLivesCircle(it, "0", "10")
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<LivesCircle>(mContext!!) {
                        override fun success(t: LivesCircle) {
                            mView?.updateData(t.content!!)
                            mView?.setLastPage(t.isLast, 0)

                        }
                    })
        }
    }

    fun getLivesCircleByID(page: String, size: String) {
        mContext?.let {
            mModel.getLivesCircle(it, "0", "10")
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<LivesCircle>(mContext!!) {
                        override fun success(t: LivesCircle) {
                            mView?.updateData(t.content!!)
                        }
                    })
        }
    }

    /**
     * @methodName： created by liushaoxiang on 2019/4/23 3:07 PM.
     * @description：点赞
     */
    fun praise(circleoffriendsId: String,position:Int) {
        val map = LinkedHashMap<String, String>()
        map["circleoffriendsId"] =circleoffriendsId
        val requestBody = ProjectUtils.getRequestBody(map)
        mContext?.let {
            mModel.praise(it, requestBody)
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserverNoDialog<ResponseBody>(mContext!!) {
                        override fun success(t: ResponseBody) {
                            mView?.praiseSuccess(position)
                        }
                    })
        }
    }


}