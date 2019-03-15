package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import com.gkzxhn.helpout.R
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.default_top.*

/**
 * @classname：意见反馈
 * @author：f
 * @date：2019/3/14 5:43 PM
 * @description：
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

