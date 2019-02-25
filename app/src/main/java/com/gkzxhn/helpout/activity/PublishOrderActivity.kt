package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.text.Html
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.alipay.sdk.app.PayTask
import com.gkzxhn.helpout.BuildConfig
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.IntentConstants
import com.gkzxhn.helpout.customview.PaySelectPopupWindow
import com.gkzxhn.helpout.entity.UIInfo.LawChannel
import com.gkzxhn.helpout.extensions.dp2px
import com.gkzxhn.helpout.presenter.PublishOrderPresenter
import com.gkzxhn.helpout.utils.HtmlTextUtils
import com.gkzxhn.helpout.utils.PayResult
import com.gkzxhn.helpout.utils.showBottomDialog
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
                et_reward.text.toString().trim().toDouble()
            } catch (e: Exception) {
                0.0
            }
            if (!cb_use_contract.isChecked) {
                showToast(getString(R.string.please_agree_laws_contract))
                return@setOnClickListener
            }
            if (!BuildConfig.DEBUG && 20 > reward) {
                showToast(getString(R.string.cost20_at_least))
                return@setOnClickListener
            } else if (reward <= 0) {
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

    override fun showPayPop(reward: Double) {
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
                                    mPresenter.getAliOrder()
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

    /**
     * 支付宝支付
     */
    override fun alipay(orderInfo: String) {

        val payRunnable = Runnable {
            val alipay = PayTask(this@PublishOrderActivity)
            val result = alipay.payV2(orderInfo, true)
            val msg = Message()
            msg.what = SDK_PAY_FLAG
            msg.obj = result
            mHandler.sendMessage(msg)
        }
        // 必须异步调用
        val payThread = Thread(payRunnable)
        payThread.start()
    }

    private val SDK_PAY_FLAG = 1

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SDK_PAY_FLAG -> {
                    val payResult = PayResult(msg.obj as Map<String, String>)
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    val resultInfo = payResult.getResult()// 同步返回需要验证的信息
                    val resultStatus = payResult.resultStatus
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        popupWindow?.dismiss()
//                        FamilyCallBalanceActivity.launch(this@BuyCardActivity)
//                        close()
//                        showToast(getString(R.string.pay_success))
                        showPaySuccess()
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        Toast.makeText(this@BuyCardActivity, getString(R.string.pay_failure), Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                }
            }
        }
    }

    fun showPaySuccess() {
        val confirmDialog = LayoutInflater.from(this).inflate(R.layout.bottom_confirm_dialog, null)
        showBottomDialog(confirmDialog)
    }
}