package com.gkzxhn.helpout.activity

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import com.gkzxhn.helpout.BuildConfig
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.IntentConstants
import com.gkzxhn.helpout.customview.PaySelectPopupWindow
import com.gkzxhn.helpout.entity.UIInfo.LawChannel
import com.gkzxhn.helpout.extensions.dp2px
import com.gkzxhn.helpout.presenter.PublishOrderPresenter
import com.gkzxhn.helpout.utils.HtmlTextUtils
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.view.PublishOrderView
import kotlinx.android.synthetic.main.activity_publish_order.*
import kotlinx.android.synthetic.main.default_top.*

class PublishOrderActivity : BaseActivity(), PublishOrderView {

    private lateinit var mPresenter: PublishOrderPresenter

    private lateinit var orderCategory: String

    companion object {
        fun launch(context: Context, intentId: String) {
            val intent = Intent(context, PublishOrderActivity::class.java)
            intent.putExtra(IntentConstants.ID, intentId)
            context.startActivity(intent)
        }
    }

    override fun init() {
        mPresenter = PublishOrderPresenter(this, this)
        initToolbar()
        initContent()
        initListener()
    }

    override fun provideContentViewId(): Int {
        return R.layout.activity_publish_order
    }

    private fun initToolbar() {
        tv_default_top_title.text = getString(R.string.publish_order)
        iv_default_top_back.setOnClickListener { finish() }
    }

    private fun initListener() {
        bt_publish_order.setOnClickListener {
            val reward = try {
                et_reward.text.toString().trim().toInt()
            } catch (e: Exception) {
                0
            }
            if (!cb_use_contract.isChecked) {
                showToast(getString(R.string.please_agree_laws_contract))
                return@setOnClickListener
            }
            if (!BuildConfig.DEBUG && reward < 20) {
                showToast(getString(R.string.cost20_at_least))
                return@setOnClickListener
            }else if (reward <= 0){
                showToast(getString(R.string.please_enter_price))
                return@setOnClickListener
            }
            mPresenter.publishOrder(orderCategory, reward)
        }
    }

    private fun initContent() {
        orderCategory = intent.getStringExtra(IntentConstants.ID)
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
    }

    lateinit var popupWindow: PaySelectPopupWindow
    var payType = 0 //支付方式: 0 支付宝 1 微信

    override fun showPayPop(reward: Int) {
        popupWindow = PaySelectPopupWindow(this, object : PaySelectPopupWindow.onItemClickListener {
            override fun onClick(popupWindow: PopupWindow, view: View) {
                when (view.id) {
                    R.id.fl_close -> {
                        popupWindow.dismiss()
                    }
                    R.id.ll_alipay -> {
                        payType = 0
                    }
                    R.id.ll_wx_pay -> {
                        payType = 1
                    }
                    R.id.bt_pay_confirm -> {
                        when (payType) {
                            0 -> {
                                //支付宝
                                if (BuildConfig.DEBUG) {
                                    //                                        amount = 0.01F
                                }
                            }
                            1 -> {
                                //微信
                                if (BuildConfig.DEBUG) {
//                                    amount = 0.01
                                }
                            }
                            else -> {
                            }
                        }
                    }
                    else -> {
                    }
                }
            }
        })
        popupWindow?.setBackgroundAlpha(0.5f)
        (popupWindow as PaySelectPopupWindow).setAmount(reward.toDouble())
        popupWindow?.showAtLocation(ll_content, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                0, ((20f).dp2px()).toInt())
    }
}