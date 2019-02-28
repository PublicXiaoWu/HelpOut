package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.ScrollView
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.common.IntentConstants
import com.gkzxhn.helpout.customview.RatingBarView
import com.gkzxhn.helpout.entity.CommentInfo
import com.gkzxhn.helpout.entity.CustomerOrderDetailInfo
import com.gkzxhn.helpout.entity.UIInfo.LawChannel
import com.gkzxhn.helpout.presenter.CustomerOrderDetailPresenter
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.StringUtils
import com.gkzxhn.helpout.utils.showToast
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
    //律师username
    private var lawUsername: String? = null
    private var ratingScore = 0

    private lateinit var mPresenter: CustomerOrderDetailPresenter

    override fun init() {
        mPresenter = CustomerOrderDetailPresenter(this, this)
        initToolbar()
        initContent()
        initListener()
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
                scrollview_order.post {
                    scrollview_order.fullScroll(ScrollView.FOCUS_DOWN)
                    hasScrollDown = true
                    et_service_comment.requestFocus()
                }
            } else {
            }
        }
    }

    private fun initContent() {
        orderId = intent.getStringExtra(IntentConstants.ID)
        //"f626cd62-e278-4cb3-9c39-33c114a252f9"
        mPresenter.getCustomerOrderDetail(orderId)
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
        //订单金额
        val reward = "¥${customerOrderDetailInfo.reward.toString()}"
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
        var btn2Visible = View.GONE
        when (customerOrderDetailInfo.status) {
            Constants.ORDER_STATE_PENDING_PAYMENT -> {
                //待支付
                orderHeaderVisible = View.VISIBLE
                orderIntroHeaderVisible = View.GONE
                lawServiceCommentVisible = View.GONE
                lawServiceVisible = View.GONE
                btn2Visible = View.GONE

                btn1Str = getString(R.string.delete_order)
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
                mPresenter.getComments(orderId)
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
                mPresenter.deleteOrder(orderId)
            } else if (btn1Str == getString(R.string.cancel_order)) {
                mPresenter.cancelOrder(orderId)
            } else {
                lawUsername?.let { it1 ->
                    //                    mPresenter.getImInfo(it1)
                    //模拟通话
                    mPresenter.mockVideoChart(orderId)
                }
            }
        }
        bt_order_2.visibility = btn2Visible
        bt_order_2.setOnClickListener {
            if (ratingScore == 0) {
                showToast(getString(R.string.please_score_the_server))
                return@setOnClickListener
            }
            val content = et_service_comment.text.toString()
            mPresenter.endOrder(orderId, content, ratingScore, rb_solve_problem_yes.isChecked)
        }
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
}