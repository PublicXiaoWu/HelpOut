package com.gkzxhn.helpout.fragment

import android.content.Intent
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.LegalConsultingActivity
import kotlinx.android.synthetic.main.home_fragment.*

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/2/20 4:59 PM
 * @description：首页
 */
class HomeFragment : BaseFragment() {

    override fun init() {
    }

    override fun initListener() {
        iv_home_legal_advice.setOnClickListener {
            startActivity(Intent(context, LegalConsultingActivity::class.java))
        }

    }

    override fun provideContentViewId(): Int {
        return R.layout.home_fragment
    }

}
