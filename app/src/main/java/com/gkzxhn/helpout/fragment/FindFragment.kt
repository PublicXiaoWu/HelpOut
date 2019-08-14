package com.gkzxhn.helpout.fragment

import android.content.Intent
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.LivesCircleActivity
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.entity.LivesCircleNew
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.logE
import com.gkzxhn.helpout.utils.showToast
import kotlinx.android.synthetic.main.find_fragment.*
import rx.android.schedulers.AndroidSchedulers

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

        /****** 收到 已经加载过生活圈了 通知发现页面刷新新的未读信息 ******/
        RxBus.instance.toObserverable(LivesCircleNew::class.java)
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (!it.username.isNullOrEmpty()) {
                        iv_find_lives_circle_image.visibility = View.VISIBLE
                        v_find_lives_circle_point.visibility = View.VISIBLE
                        context?.let { it1 -> ProjectUtils.loadRoundImageByUserName(it1, it.username, iv_find_lives_circle_image) }
                    } else {
                        iv_find_lives_circle_image.visibility = View.GONE
                        v_find_lives_circle_point.visibility = View.GONE

                    }
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
            context?.showToast(getString(R.string.please_expectantly))
        }
    }

}
