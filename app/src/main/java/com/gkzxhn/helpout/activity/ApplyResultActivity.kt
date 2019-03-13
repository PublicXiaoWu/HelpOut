package com.gkzxhn.helpout.activity

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.widget.LinearLayout
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.extensions.dp2px
import kotlinx.android.synthetic.main.default_top.*
import kotlinx.android.synthetic.main.entry.*

class ApplyResultActivity : BaseActivity() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, ApplyResultActivity::class.java))
        }
    }

    override fun init() {
        tv_default_top_title.text = getString(R.string.feed_back)
        iv_default_top_back.setOnClickListener { finish() }
        tv_down.text = getString(R.string.close)
        tv_pay_status.text = "反馈成功"
        tv_pay_status.setTextColor(resources.getColor(R.color.dark_blue))
        tv_pay_status.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_large))
        val params = tv_pay_status.layoutParams as LinearLayout.LayoutParams
        params.topMargin = 30f.dp2px().toInt()
        tv_pay_status.layoutParams = params

        tv_down.setOnClickListener { finish() }
    }

    override fun provideContentViewId(): Int {
        return R.layout.entry
    }
}