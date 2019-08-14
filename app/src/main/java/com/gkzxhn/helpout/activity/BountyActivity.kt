package com.gkzxhn.helpout.activity

import android.app.Activity
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.presenter.BountyPresenter
import com.gkzxhn.helpout.view.BountyView
import kotlinx.android.synthetic.main.activity_bounty.*
import kotlinx.android.synthetic.main.default_top.*

/**
 * @classname：BountyActivity
 * @author：liushaoxiang
 * @date：2018/10/11 1:39 PM
 * @description：账户余额
 */
class BountyActivity : BaseActivity(), BountyView {

    override fun finishActivity() {
        finish()
    }

    lateinit var mPresenter: BountyPresenter

    override fun provideContentViewId(): Int {
        return R.layout.activity_bounty
    }

    override fun init() {
        initTopTitle()
        mPresenter = BountyPresenter(this, this)
        mPresenter.getLawyersInfo()
    }

    private fun initTopTitle() {
        tv_default_top_title.text = getString(R.string.balance)

        iv_default_top_back.setOnClickListener {
            finish()
        }
    }

    fun onClickBounty(view: View) {
        when (view.id) {
        /****** 提现 ******/
            R.id.v_bounty_get_money -> {
                mPresenter.getMoney()

            }
        /****** 绑定支付宝 ******/
            R.id.v_bounty_get_alipay -> {
                mPresenter.bindOrUnbind()
            }
        }
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun setMoney(money: String) {
        tv_bounty_money.text = money
    }

    override fun setBindState(bindState: String) {
        tv_bounty_bind_state.text = bindState
    }

    override fun changeBingState(bindState: Boolean) {
        if (bindState) {
            tv_bounty_bind_state.text = getString(R.string.bind)
            tv_bounty_bind_state.setTextColor(resources.getColor(R.color.bind_blue))
        } else {
            tv_bounty_bind_state.text = getString(R.string.un_bind)
            tv_bounty_bind_state.setTextColor(resources.getColor(R.color.bind_gary))
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter.getLawyersInfo()
    }
}