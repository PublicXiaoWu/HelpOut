package com.gkzxhn.helpout.fragment

import android.content.Intent
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.LivesCircleActivity
import com.gkzxhn.helpout.utils.showToast
import kotlinx.android.synthetic.main.find_fragment.*

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


}
