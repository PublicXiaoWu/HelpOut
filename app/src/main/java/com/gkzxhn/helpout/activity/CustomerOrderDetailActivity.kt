package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.common.IntentConstants
import com.gkzxhn.helpout.entity.CustomerOrderDetailInfo
import com.gkzxhn.helpout.entity.UIInfo.LawChannel
import com.gkzxhn.helpout.presenter.CustomerOrderDetailPresenter
import com.gkzxhn.helpout.utils.StringUtils
import com.gkzxhn.helpout.view.CustomerOrderDetailView
import kotlinx.android.synthetic.main.activity_customer_order_detail.*
import kotlinx.android.synthetic.main.default_top.*
import kotlinx.android.synthetic.main.layout_order_content.*
import kotlinx.android.synthetic.main.layout_order_head.*

class CustomerOrderDetailActivity : BaseActivity(), CustomerOrderDetailView {

    companion object {
        fun launch(context: Context, intentId: String) {
            val intent = Intent(context, CustomerOrderDetailActivity::class.java)
            intent.putExtra(IntentConstants.ID, intentId)
            context.startActivity(intent)
        }
    }

    private lateinit var orderId: String

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

    private fun initListener() {
        /*bt_publish_order.setOnClickListener {
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
        }*/
    }

    private fun initContent() {
        orderId = intent.getStringExtra(IntentConstants.ID)
        //"f626cd62-e278-4cb3-9c39-33c114a252f9"
        mPresenter.getCustomerOrderDetail(orderId)
    }

    @SuppressLint("SetTextI18n")
    override fun setData(customerOrderDetailInfo: CustomerOrderDetailInfo) {
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
        when (customerOrderDetailInfo.status) {
            Constants.ORDER_STATE_PENDING_PAYMENT, Constants.ORDER_STATE_PENDING_RECEIVING -> {
                //待支付, 待接单
                layout_order_head.visibility = View.VISIBLE
                layout_order_intro_head.visibility = View.GONE
                customerOrderDetailInfo.category?.let {
                    LawChannel.find(it)?.let { it1 ->
                        tv_order_type1.text = it1.name
                        it1.imgRes?.let { iv_oder_icon.setImageResource(it) }
                    }
                }
                tv_order_time.text = StringUtils.parseDate(customerOrderDetailInfo.createdTime)
                tv_order_number.text = "${getString(R.string.serial_number)}: ${customerOrderDetailInfo.number}"
                tv_order_price.text = "¥${customerOrderDetailInfo.reward.toString()}"

                tv_color_title.text = getString(R.string.waiting_4_receiving)
                tv_order_body1.visibility = View.GONE
                tv_order_body2.visibility = View.GONE
            }
            Constants.ORDER_STATE_ACCEPTED, Constants.ORDER_STATE_CANCELLED -> {
                //已接单, 已取消
                layout_order_head.visibility = View.GONE
                layout_order_intro_head.visibility = View.VISIBLE


            }
            else -> {
                layout_order_head.visibility = View.GONE
                layout_order_intro_head.visibility = View.VISIBLE
            }
        }
    }

}