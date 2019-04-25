package com.gkzxhn.helpout.fragment

import android.content.Intent
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.LivesCircleActivity
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.entity.LivesCircleNew
import com.gkzxhn.helpout.entity.RxBusBean
import com.gkzxhn.helpout.net.HttpObserverNoDialog
import com.gkzxhn.helpout.net.RetrofitClientChat
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.logE
import com.gkzxhn.helpout.utils.showToast
import kotlinx.android.synthetic.main.find_fragment.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @classname：发现页面
 * @author：liushaoxiang
 * @date：2019/4/12 1:59 PM
 * @description：
 */

class FindFragment : BaseFragment() {

    override fun provideContentViewId(): Int {
        return R.layout.find_fragment
    }

    override fun init() {
        getLivesCircleNew()

        /****** 收到 已经加载过生活圈了 通知发现页面刷新新的未读信息 ******/
        RxBus.instance.toObserverable(RxBusBean.ChangeFindUnRead::class.java)
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    getLivesCircleNew()
                }, {
                    it.message.toString().logE(this)
                })

    }


    override fun initListener() {
        v_lives_circle_bg.setOnClickListener {
            val intent = Intent(context, LivesCircleActivity::class.java)
            intent.putExtra("LivesCircleType", 1)
            context?.startActivity(intent)
        }
        v_scan_bg.setOnClickListener {
            context?.showToast("敬请期待")
        }
    }


    /**
     * @methodName： created by liushaoxiang on 2019/4/25 9:46 AM.
     * @description：获取最新未看生活圈
     */
    fun getLivesCircleNew() {
        mCompositeSubscription.add(context?.let {
            RetrofitClientChat
                    .getInstance(it).mApi.getLivesCircleNew()
                    .subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserverNoDialog<LivesCircleNew>(it) {
                        override fun success(t: LivesCircleNew) {
                            if (!t.username.isNullOrEmpty()) {
                                iv_find_lives_circle_image.visibility = View.VISIBLE
                                v_find_lives_circle_point.visibility = View.VISIBLE
                                ProjectUtils.loadRoundImageByUserName(it, t.username, iv_find_lives_circle_image)
                            } else {
                                iv_find_lives_circle_image.visibility = View.GONE
                                v_find_lives_circle_point.visibility = View.GONE

                            }
                        }
                    })
        })
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            getLivesCircleNew()
        }
        super.setUserVisibleHint(isVisibleToUser)
    }


}
