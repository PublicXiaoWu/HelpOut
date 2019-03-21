package com.gkzxhn.helpout.activity

import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.utils.StringUtils
import kotlinx.android.synthetic.main.activity_withdraw_3.*
import kotlinx.android.synthetic.main.default_top.*

/**
 * @classname：WithdrawActivity
 * @author：liushaoxiang
 * @date：2018/10/16 2:10 PM
 * @description：提现
 */
class WithdrawThirdActivity : BaseActivity() {

    override fun init() {
        initTopTitle()

        val account = intent.getStringExtra("pay_Account")
        val payName = intent.getStringExtra("pay_Name")
        val moneyString = intent.getStringExtra("money")
        val type = intent.getIntExtra("pay_type", 0)
        tv_withdraw_3_to_time.text = StringUtils.MstoDate((System.currentTimeMillis()).toString())
        tv_withdraw_3_time.text = StringUtils.MstoDate((System.currentTimeMillis() + 7200000).toString())
        tv_withdraw_3_acount.text = account
        tv_withdraw_3_name.text = payName
        val money = moneyString.toDouble() * 0.7
        val formatMoney = StringUtils.formatStringTwo(money)
        tv_withdraw_3_money.text = "￥$formatMoney"
        tv_withdraw_3_type.text = if (type == 1) {
            "支付宝账户"
        } else {
            "微信"
        }
    }

    override fun provideContentViewId(): Int {
        return R.layout.activity_withdraw_3
    }

    private fun initTopTitle() {
        tv_default_top_title.text = "提现详情"
        iv_default_top_back.setOnClickListener {
            finish()
        }
    }

}