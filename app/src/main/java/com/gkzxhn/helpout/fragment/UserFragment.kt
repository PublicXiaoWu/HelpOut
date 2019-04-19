package com.gkzxhn.helpout.fragment

import android.content.Intent
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.*
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.entity.AccountInfo
import com.gkzxhn.helpout.entity.LawyersInfo
import com.gkzxhn.helpout.entity.RxBusBean
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.HttpObserverNoDialog
import com.gkzxhn.helpout.net.RetrofitClient
import com.gkzxhn.helpout.net.RetrofitClientPublic
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.StringUtils
import com.gkzxhn.helpout.utils.logE
import kotlinx.android.synthetic.main.user_fragment.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


/**
 * Explanation: 个人中心
 * @author LSX
 *    -----2018/9/7
 */

class UserFragment : BaseFragment(), View.OnClickListener {
    var accountInfo: AccountInfo? = null

    override fun provideContentViewId(): Int {
        return R.layout.user_fragment
    }

    override fun init() {
        getAccountInfo()
//        getLawyersState()
        getLawyersInfo()

        /****** 接受控件小红点的消息 ******/
        RxBus.instance.toObserverable(RxBusBean.HomeTopRedPoint::class.java)
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.show) {
                        getAccountInfo()
                        getLawyersState()
                    }
                }, {
                    it.message.toString().logE(this)
                })
    }

    override fun initListener() {
        v_user_my_money_bg.setOnClickListener(this)
        v_user_rz_bg.setOnClickListener(this)
        v_user_set_bg.setOnClickListener(this)
        v_user_top_bg.setOnClickListener(this)
        v_user_friend_list_bg.setOnClickListener(this)
        v_user_my_lives_circle_bg.setOnClickListener(this)
        v_user_my_zx_bg.setOnClickListener(this)
        v_user_bill_bg.setOnClickListener(this)
        /****** 给个人信息栏设置背影触摸变化 ******/
        ProjectUtils.addViewTouchChange(v_user_top_bg)

    }


    override fun onClick(v: View) {
        when (v.id) {
            /****** 通讯录 ******/
            R.id.v_user_friend_list_bg -> {
                context?.startActivity(Intent(context, FriendListActivity::class.java))
            }
            /****** 我的生活圈 ******/
            R.id.v_user_my_lives_circle_bg -> {
                context?.startActivity(Intent(context, LivesCircleActivity::class.java))

            }
            /****** 我的咨询 ******/
            R.id.v_user_my_zx_bg -> {
                context?.startActivity(Intent(context, MyLegalConsultingActivity::class.java))
            }
            R.id.v_user_my_money_bg -> {
                context?.startActivity(Intent(context, BountyActivity::class.java))
            }
            R.id.v_user_rz_bg -> {
                context?.startActivity(Intent(context, ExpertActivity::class.java))
            }
//            设置
            R.id.v_user_set_bg -> {
                context?.startActivity(Intent(context, SettingActivity::class.java))
            }
//            账单
            R.id.v_user_bill_bg -> {
                startActivity(Intent(context, MoneyListActivity::class.java))
            }
//            个人信息栏
            R.id.v_user_top_bg -> {
                val intent = Intent(context, UserSettingActivity::class.java)
                intent.putExtra("name", if (accountInfo != null) accountInfo?.nickname else "")
                intent.putExtra("phoneNumber", if (accountInfo != null) accountInfo?.phoneNumber else "")
                intent.putExtra("haveAvatar", !accountInfo?.avatar.isNullOrEmpty())
                context?.startActivity(intent)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        init()
    }

    /**
     * @methodName： created by liushaoxiang on 2018/10/22 3:31 PM.
     * @description：获取律师信息
     */
    private fun getLawyersInfo() {
        context?.let {
            mCompositeSubscription.add(RetrofitClient.getInstance(it).mApi.getLawyersInfo()
                    .subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<LawyersInfo>(it) {
                        override fun success(t: LawyersInfo) {
                            App.EDIT.putString(Constants.SP_CERTIFICATIONSTATUS, t.certificationStatus)?.commit()
                            val lawyerState = t.certificationStatus == Constants.CERTIFIED
                            loadUIByLawyerState(lawyerState)
                            if (lawyerState) {
                                tv_user_money.text = "￥" + t.rewardAmount
                            }
                        }
                    }))
        }
    }

    /**
     * @methodName： created by liushaoxiang on 2019/2/26 12:00 PM.
     * @description：获取律师认证状态
     */
    private fun getLawyersState() {
        context?.let {
            mCompositeSubscription.add(RetrofitClient.getInstance(it).mApi.getLawyersState()
                    .subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<LawyersInfo>(it) {
                        override fun success(t: LawyersInfo) {
                            loadUIByLawyerState(t.lawyer!!)
                        }
                    }))

        }
    }

    /**
     * @methodName： created by liushaoxiang on 2019/2/26 1:51 PM.
     * @description：加载用户或者律师不同的UI
     */
    private fun loadUIByLawyerState(lawyer: Boolean) {
        if (lawyer) {
            v_user_info_rz_bg.visibility = View.VISIBLE
            iv_user_info_rz.visibility = View.VISIBLE
            tv_user_info_rz.visibility = View.VISIBLE

            /****** 金额那一栏 ******/
            v_user_my_money_bg.visibility = View.VISIBLE
            iv_user_get_money_start.visibility = View.VISIBLE
            tv_user_get_money.visibility = View.VISIBLE
            tv_user_money.visibility = View.VISIBLE
            iv_user_get_money_end.visibility = View.VISIBLE
            v_user_my_money_line_bg.visibility = View.VISIBLE
            v_user_my_money.visibility = View.VISIBLE

        } else {
            /****** 用户 ******/

            v_user_info_rz_bg.visibility = View.INVISIBLE
            iv_user_info_rz.visibility = View.INVISIBLE
            tv_user_info_rz.visibility = View.INVISIBLE

            v_user_my_money_bg.visibility = View.GONE
            iv_user_get_money_start.visibility = View.GONE
            tv_user_get_money.visibility = View.GONE
            tv_user_money.visibility = View.GONE
            iv_user_get_money_end.visibility = View.GONE
            v_user_my_money_line_bg.visibility = View.GONE
            v_user_my_money.visibility = View.GONE

        }

    }

    /**
     * @methodName： created by liushaoxiang on 2018/10/22 3:31 PM.
     * @description：获取我的账号明细
     */
    private fun getAccountInfo() {
        context?.let {
            mCompositeSubscription.add(RetrofitClientPublic.getInstance(it).mApi
                    ?.getAccountInfo()
                    ?.subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserverNoDialog<AccountInfo>(it) {
                        override fun success(t: AccountInfo) {
                            loadUI(t)
                            accountInfo = t
                        }
                    }))
        }
    }


    /**
     * @methodName： created by liushaoxiang on 2018/10/26 3:46 PM.
     * @description：处理UI数据
     */
    private fun loadUI(date: AccountInfo) {
        tv_user_phone.text = StringUtils.phoneChange(date.phoneNumber!!)
        tv_user_name.text = date.nickname
        App.EDIT.putString(Constants.SP_PHONE, date.phoneNumber)?.commit()
        App.EDIT.putString(Constants.SP_NAME, date.nickname)?.commit()
        ProjectUtils.loadMyIcon(context, iv_user_icon)
    }

}
