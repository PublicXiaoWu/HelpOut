package com.gkzxhn.helpout.activity

import android.content.Context
import android.content.Intent
import android.text.Html
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.IntentConstants
import com.gkzxhn.helpout.entity.UIInfo.LawChannel
import com.gkzxhn.helpout.utils.HtmlTextUtils
import kotlinx.android.synthetic.main.activity_publish_order.*
import kotlinx.android.synthetic.main.default_top.*

class PublishOrderActivity : BaseActivity() {

    companion object {
        fun launch(context: Context, intentId: String) {
            val intent = Intent(context, PublishOrderActivity::class.java)
            intent.putExtra(IntentConstants.ID, intentId)
            context.startActivity(intent)
        }
    }

    override fun init() {
        initToolbar()
        initContent()
    }

    override fun provideContentViewId(): Int {
        return R.layout.activity_publish_order
    }

    private fun initToolbar() {
        tv_default_top_title.text = getString(R.string.publish_order)
        iv_default_top_back.setOnClickListener { finish() }
    }

    private fun initContent() {
        val orderCategory = intent.getStringExtra(IntentConstants.ID)
        tv_category.text = LawChannel.find(orderCategory)?.name
        tv_fee_tips.text = Html.fromHtml(
                HtmlTextUtils.getColorHtmlString(
                        Pair(getString(R.string.more_consulting_fee), "#666666"),
                        Pair(getString(R.string.efficient), "#FF8A07"),
                        Pair(getString(R.string.and_yu), "#666666"),
                        Pair(getString(R.string.quality), "#FF8A07"),
                        Pair("${getString(R.string.higher)}!", "#666666")
                )
        )
        tv_price_tips.text = Html.fromHtml(
                HtmlTextUtils.getColorHtmlString(
                        Pair("${getString(R.string.price_least_tips)},${getString(R.string.reply)}", "#666666"),
                        Pair("20${getString(R.string.yuan)}", "#FF8A07")
                )
        )
    }
}