package com.gkzxhn.helpout.activity

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.extensions.dp2px
import kotlinx.android.synthetic.main.default_top.*
import kotlinx.android.synthetic.main.entry.*

/**
 * @classname：意见反馈
 * @author：f
 * @date：2019/3/14 5:28 PM
 * @description：
 */
class ApplyResultActivity : BaseActivity() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, ApplyResultActivity::class.java))
        }
    }

    override fun init() {
        initTop()

        tv_pay_status.text = "反馈成功"
        tv_pay_status.setTextColor(resources.getColor(R.color.dark_blue))
        tv_pay_status.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_large))
        val params = tv_pay_status.layoutParams as LinearLayout.LayoutParams
        params.topMargin = 30f.dp2px().toInt()
        tv_pay_status.layoutParams = params

    }

    private fun initTop() {
        tv_default_top_title.text = getString(R.string.feed_back)
        iv_default_top_back.setOnClickListener { finish() }
        tv_default_top_end.visibility=View.VISIBLE
        tv_default_top_end.text = getString(R.string.close)
        tv_default_top_end.setOnClickListener { finish() }

    }

    override fun provideContentViewId(): Int {
        return R.layout.entry
    }
}