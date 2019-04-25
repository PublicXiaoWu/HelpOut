package com.gkzxhn.helpout.presenter

import android.content.Context
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.entity.LivesCircle
import com.gkzxhn.helpout.entity.LivesCircleDetails
import com.gkzxhn.helpout.entity.RxBusBean
import com.gkzxhn.helpout.model.ILivesCircleModel
import com.gkzxhn.helpout.model.iml.LivesCircleModel
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.HttpObserverNoDialog
import com.gkzxhn.helpout.net.RetrofitClientChat
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.view.LivesCircleView
import okhttp3.ResponseBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @classname：生活圈
 * @author：liushaoxiang
 * @date：2019/4/22 3:34 PM
 * @description：
 */

class LivesCirclePresenter(context: Context, view: LivesCircleView) : BasePresenter<ILivesCircleModel, LivesCircleView>(context, LivesCircleModel(), view) {

    fun getLivesCircle(page: String, size: String) {
        mContext?.let {
            mModel.getLivesCircle(it, page, "100")
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<LivesCircle>(mContext!!) {
                        override fun success(t: LivesCircle) {
                            mView?.updateData(t.content!!)
                            mView?.setLastPage(t.isLast, 0)
                            /****** 已经加载过生活圈了 通知发现页面刷新新的未读信息 ******/
                           RxBus.instance.post(RxBusBean.ChangeFindUnRead())
                        }
                    })
        }
    }


    fun getMyLivesCircle(page: String, size: String) {
        /****** 我的生活圈已经进来过了 通知红点消失 ******/
        RxBus.instance.post(RxBusBean.MyLivesCirclePoint(false))
        mContext?.let {
            mModel.getMyLivesCircle(it, page, "100")
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

    fun getLivesCircleByUserName(userName:String,page: String, size: String) {
        mContext?.let {
            mModel.getLivesCircleByUserName(it,userName, page, "100")
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
    fun praise(circleoffriendsId: String, position: Int) {
        val map = LinkedHashMap<String, String>()
        map["circleoffriendsId"] = circleoffriendsId
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

    /**
     * @methodName： created by liushaoxiang on 2019/4/24 11:32 AM.
     * @description：生活圈明细
     */
    fun getLivesCircleDetails(livesCircleId: String) {
        mContext?.let {
            RetrofitClientChat
                    .getInstance(it).mApi.getLivesCircleDetails(livesCircleId)
                    .subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<LivesCircleDetails>(it) {
                        override fun success(t: LivesCircleDetails) {
                            mView?.loadLivesCircleDetailsUI(t)
                        }
                    })
        }
    }

    /**
     * @methodName： created by liushaoxiang on 2019/4/23 3:07 PM.
     * @description：评论
     */
    fun comment(content: String, circleoffriendsId: String) {
        val map = LinkedHashMap<String, String>()
        map["content"] = content
        map["circleoffriendsId"] = circleoffriendsId
        val requestBody = ProjectUtils.getRequestBody(map)
        mContext?.let {
            mModel.comment(it, requestBody)
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserverNoDialog<ResponseBody>(mContext!!) {
                        override fun success(t: ResponseBody) {
                            getLivesCircleDetails(circleoffriendsId)
                        }
                    })
        }
    }


}