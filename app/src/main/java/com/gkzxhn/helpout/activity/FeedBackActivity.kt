package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import com.gkzxhn.helpout.R
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.default_top.*

/**
 * @classname：SettingActivtiy
 * @author：liushaoxiang
 * @date：2018/10/11 3:52 PM
 * @description：设置
 */
class FeedBackActivity : BaseActivity() {
    override fun provideContentViewId(): Int {
        return R.layout.activity_feedback
    }

    @SuppressLint("SetTextI18n")
    override fun init() {
        initTopTitle()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        rv_feedback.adapter
    }

    private fun initTopTitle() {
        tv_default_top_title.text = getString(R.string.feedback)
        iv_default_top_back.setOnClickListener {
            finish()
        }

    }
}

