package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.TextView
import com.alipay.sdk.app.PayTask
import com.gkzxhn.helpout.BuildConfig
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.common.IntentConstants
import com.gkzxhn.helpout.customview.PaySelectPopupWindow
import com.gkzxhn.helpout.customview.RatingBarView
import com.gkzxhn.helpout.entity.CommentInfo
import com.gkzxhn.helpout.entity.CustomerOrderDetailInfo
import com.gkzxhn.helpout.entity.UIInfo.LawChannel
import com.gkzxhn.helpout.presenter.CustomerOrderDetailPresenter
import com.gkzxhn.helpout.utils.*
import com.gkzxhn.helpout.view.CustomerOrderDetailView
import kotlinx.android.synthetic.main.activity_customer_order_detail.*
import kotlinx.android.synthetic.main.default_top.*
import kotlinx.android.synthetic.main.layout_law_service.*
import kotlinx.android.synthetic.main.layout_law_service_comment.*
import kotlinx.android.synthetic.main.layout_order_content.*
import kotlinx.android.synthetic.main.layout_order_head.*
import kotlinx.android.synthetic.main.layout_order_intro_head.*

class CustomerOrderDetailActivity : BaseActivity(), CustomerOrderDetailView {

    companion object {
        fun launch(context: Context, intentId: String) {
            val intent = Intent(context, CustomerOrderDetailActivity::class.java)
            intent.putExtra(IntentConstants.ID, intentId)
            context.startActivity(intent)
        }
    }

    //订单id
    private lateinit var orderId: String

    private var amount: Double = 0.0
    //律师username
    private var lawUsername: String? = null
    private var ratingScore = 0

    private lateinit var mPresenter: CustomerOrderDetailPresenter

    override fun init() {
        mPresenter = CustomerOrderDetailPresenter(this, this)
        initToolbar()
        initContent()
        initListener()
        mCompositeSubscription.add(mPresenter.subscribePay())
    }

    override fun provideContentViewId(): Int {
        return R.layout.activity_customer_order_detail
    }

    private fun initToolbar() {
        tv_default_top_title.text = getString(R.string.order_detail)
        iv_default_top_back.setOnClickListener { finish() }
    }

    var hasScrollDown = false

    private fun initListener() {
        rbv_service_comment.setOnRatingListener(object : RatingBarView.OnRatingListener {
            override fun onRating(bindObject: Any?, ratingScore: Int) {
                this@CustomerOrderDetailActivity.ratingScore = ratingScore
            }
        })
        et_service_comment.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && !hasScrollDown) {
                scrollview_order.postDelayed({
                    scrollview_order.fullScroll(ScrollView.FOCUS_DOWN)
                    hasScrollDown = true
//                    et_service_comment.requestFocus()
                }, 200)
            } else {
            }
        }
        et_service_comment.setOnClickListener {
            if (et_service_comment.isFocused) {
                scrollview_order.postDelayed({
                    scrollview_order.fullScroll(ScrollView.FOCUS_DOWN)
                    hasScrollDown = true
//                    et_service_comment.requestFocus()
                }, 200)
            }
        }
    }

    private fun initContent() {
        orderId = intent.getStringExtra(IntentConstants.ID)
        //"f626cd62-e278-4cb3-9c39-33c114a252f9"
        mCompositeSubscription.add(mPresenter.getCustomerOrderDetail(orderId))
    }

    @SuppressLint("SetTextI18n")
    override fun setData(customerOrderDetailInfo: CustomerOrderDetailInfo) {
        lawUsername = customerOrderDetailInfo.lawyer?.username
        ll_bottom_btn.visibility = View.VISIBLE
        tv_order_state.text = when (customerOrderDetailInfo.paymentStatus) {
            Constants.PAYMENT_STATE_AUTHORIZED -> {
                getString(R.string.authorized)
            }
            Constants.PAYMENT_STATE_PAID -> {
                getString(R.string.have_paid)
            }
            Constants.PAYMENT_STATE_PENDING -> {
                getString(R.string.waiting_4_pay)
            }
            Constants.PAYMENT_STATE_REFUNDED -> {
                getString(R.string.have_refunded)
            }
            Constants.PAYMENT_STATE_VOIDED -> {
                getString(R.string.have_cancelled)
            }
            else -> {
                getString(R.string.unknown_status)
            }
        }
        //订单编号
        val orderNumber = "${getString(R.string.serial_number)}: ${customerOrderDetailInfo.number}"
        //订单类别
        val orderCategory = customerOrderDetailInfo.category?.let { LawChannel.find(it) }
        amount = customerOrderDetailInfo.reward ?: 0.0
        //订单金额
        val reward = "¥${amount.toString()}"
        //律师名字
        val lawyerName = customerOrderDetailInfo.lawyer?.name ?: ""
        //执业律所
        val lawyerOffice = customerOrderDetailInfo.lawyer?.lawOffice ?: ""
        //律师评分
        val lawyerRate = customerOrderDetailInfo.lawyer?.rate
        //创建时间
        val createTime = customerOrderDetailInfo.createdTime?.let { StringUtils.parseDate(it) }
        //接单时间
        val acceptedTime = customerOrderDetailInfo.acceptedTime?.let { "${getString(R.string.accepted_time)}: ${StringUtils.parseDate(it)}" }
        //完成时间
        val finishTime = customerOrderDetailInfo.endTime?.let { "${getString(R.string.finish_time)}: ${StringUtils.parseDate(it)}" }
        //取消时间
        val canceledTime = customerOrderDetailInfo.endTime?.let { "${getString(R.string.canceled_time)}: ${StringUtils.parseDate(it)}" }
        //处理时间
        val processTime = customerOrderDetailInfo.processedTime?.let { "${getString(R.string.finish_time)}: ${StringUtils.parseDate(it)}" }

        var orderHeaderVisible = View.VISIBLE
        var orderIntroHeaderVisible = View.GONE
        var lawServiceCommentVisible = View.GONE
        var lawServiceVisible = View.GONE

        var colorTitleStr: String? = null
        var orderBodyStr1: String? = null
        var orderBodyStr2: String? = null
        var btn1Str: String? = null
        var btn2Str: String? = null
        var btn2Visible = View.GONE
        when (customerOrderDetailInfo.status) {
            Constants.ORDER_STATE_PENDING_PAYMENT -> {
                //待支付
                orderHeaderVisible = View.VISIBLE
                orderIntroHeaderVisible = View.GONE
                lawServiceCommentVisible = View.GONE
                lawServiceVisible = View.GONE
                btn2Visible = View.VISIBLE

                btn1Str = getString(R.string.delete_order)
                btn2Str = getString(R.string.pay_right_now)
            }
            Constants.ORDER_STATE_PENDING_RECEIVING -> {
                //待接单
                orderHeaderVisible = View.VISIBLE
                orderIntroHeaderVisible = View.GONE
                lawServiceCommentVisible = View.GONE
                lawServiceVisible = View.GONE
                btn2Visible = View.GONE

                colorTitleStr = getString(R.string.waiting_4_receiving)
                btn1Str = getString(R.string.cancel_order)
            }
            Constants.ORDER_STATE_ACCEPTED -> {
                //已接单
                orderHeaderVisible = View.GONE
                orderIntroHeaderVisible = View.VISIBLE
                lawServiceCommentVisible = View.GONE
                lawServiceVisible = View.GONE
                btn2Visible = View.GONE

                colorTitleStr = "${getString(R.string.have_accepted_order)}($lawyerName)"
                orderBodyStr1 = acceptedTime
                btn1Str = getString(R.string.linkup_now)
            }
            Constants.ORDER_STATE_CANCELLED -> {
                // 已取消
                orderHeaderVisible = View.VISIBLE
                orderIntroHeaderVisible = View.GONE
                lawServiceCommentVisible = View.GONE
                lawServiceVisible = View.GONE
                btn2Visible = View.GONE

                colorTitleStr = getString(R.string.have_cancelled)
                orderBodyStr1 = canceledTime
                orderBodyStr2 = getString(R.string.cancel_reason) + "：" +
                        if (customerOrderDetailInfo.isAutoEnd == true) getString(R.string.auto_cancel_in_3_days) else
                            getString(R.string.user_cancel_order)
                btn1Str = getString(R.string.delete_order)
            }
            Constants.ORDER_STATE_PROCESSING -> {
                // 处理中
                orderHeaderVisible = View.GONE
                orderIntroHeaderVisible = View.VISIBLE
                lawServiceCommentVisible = View.VISIBLE
                lawServiceVisible = View.GONE
                btn2Visible = View.VISIBLE

                colorTitleStr = "${getString(R.string.have_accepted_order)}($lawyerName)"
                orderBodyStr1 = acceptedTime
                orderBodyStr2 = processTime
                btn1Str = getString(R.string.linkup_now)
                btn2Str = getString(R.string.end_consult)
            }
            Constants.ORDER_STATE_COMPLETE, Constants.ORDER_STATE_REFUSED -> {
                //已完成, 已关闭
                orderHeaderVisible = View.GONE
                orderIntroHeaderVisible = View.VISIBLE
                lawServiceCommentVisible = View.GONE
                lawServiceVisible = View.VISIBLE
                btn2Visible = View.GONE

                colorTitleStr = getString(R.string.have_completed)
                orderBodyStr1 = acceptedTime
                orderBodyStr2 = finishTime
                btn1Str = getString(R.string.delete_order)
                mCompositeSubscription.add(mPresenter.getComments(orderId))
            }
            else -> {
                layout_order_head.visibility = View.GONE
                layout_order_intro_head.visibility = View.VISIBLE
                lawServiceCommentVisible = View.GONE
                lawServiceVisible = View.GONE
            }
        }

        layout_order_head.visibility = orderHeaderVisible
        layout_order_intro_head.visibility = orderIntroHeaderVisible
        layout_law_service.visibility = lawServiceVisible
        layout_law_service_comment.visibility = lawServiceCommentVisible

        orderCategory?.let {
            tv_order_type1.text = it.name
            it.imgRes?.let { iv_oder_icon.setImageResource(it) }
        }
        createTime?.let { tv_order_time.text = it }
        tv_order_number.text = orderNumber
        tv_order_price.text = reward

        ProjectUtils.loadRoundImageByUserName(this, lawUsername, iv_oder_intro_icon)
        tv_order_intro_name.text = lawyerName
        tv_order_intro_price.text = reward
        tv_law_firm_practicing.text = lawyerOffice
        tv_order_intro_number.text = orderNumber
        lawyerRate?.let { rbv_service_star.setStar(it, true) }

        colorTitleStr?.let { tv_color_title.text = it }
        tv_order_body1.visibility = View.VISIBLE.takeIf { orderBodyStr1 != null } ?: View.GONE
        tv_order_body1.text = orderBodyStr1
        tv_order_body2.visibility = View.VISIBLE.takeIf { orderBodyStr2 != null } ?: View.GONE
        tv_order_body2.text = orderBodyStr2

        bt_order_1.text = btn1Str
        bt_order_1.setOnClickListener {
            if (btn1Str == getString(R.string.delete_order)) {
                mCompositeSubscription.add(mPresenter.deleteOrder(orderId))
            } else if (btn1Str == getString(R.string.cancel_order)) {
                mCompositeSubscription.add(mPresenter.cancelOrder(orderId))
            } else {
                lawUsername?.let { it1 ->
                    //                    mPresenter.getImInfo(it1)
                    //模拟通话
                    mCompositeSubscription.add(mPresenter.mockVideoChart(orderId))
                }
            }
        }
        bt_order_2.visibility = btn2Visible
        bt_order_2.text = btn2Str
        bt_order_2.setOnClickListener {
            if (bt_order_2.text.toString() == getString(R.string.end_consult))
                endConsult()
            else
                showPayPop(amount)
        }
    }

    /**
     * 結束咨詢
     */
    private fun endConsult() {
        if (ratingScore == 0) {
            showToast(getString(R.string.please_score_the_server))
            return
        }
        val content = et_service_comment.text.toString()
        mCompositeSubscription.add(mPresenter.endOrder(orderId, content, ratingScore, rb_solve_problem_yes.isChecked))
    }

    /**
     * 设置律师评价数据
     */
    override fun setCommentsData(commentInfo: CommentInfo) {
        commentInfo.rate?.let { rbv_service_comment_star.setStar(it, true) }
        tv_order_server_end.text = getString(R.string.have_solved_problem).takeIf { commentInfo.isResolved == true } ?: getString(R.string.have_not_solved_problem)
        tv_order_evaluation.text =
                commentInfo.content.takeIf { !TextUtils.isEmpty(it) }?.let { getString(R.string.service_comment) + ": " + it } ?: getString(R.string.the_user_have_not_comment)

    }

    override fun close() {
        finish()
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
                                }
                                mCompositeSubscription.add(mPresenter.getAliOrder())
                            }
                            1 -> {
                                //微信
                                if (BuildConfig.DEBUG) {
//                                    amount = 0.01
                                }
                                mCompositeSubscription.add(mPresenter.getWxOrder(reward))
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
                0, 0)
    }

    /**
     * 支付宝支付
     */
    override fun alipay(orderInfo: String) {
        popupWindow.dismiss()

        val payRunnable = Runnable {
            val alipay = PayTask(this@CustomerOrderDetailActivity)
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

    override fun showPaySuccess() {
        val confirmDialog = LayoutInflater.from(this).inflate(R.layout.bottom_confirm_dialog, null)
        showBottomDialog(confirmDialog)
        confirmDialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dismissBottomDialog()
        }
        confirmDialog.findViewById<TextView>(R.id.tv_confirm).setOnClickListener {
            dismissBottomDialog()
            mPresenter.orderId?.let { it1 ->
                mCompositeSubscription.add(mPresenter.getCustomerOrderDetail(it1))
            }
        }
    }
}