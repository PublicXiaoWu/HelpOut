package com.gkzxhn.helpout.fragment

import android.content.Intent
import android.text.TextUtils
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
import com.gkzxhn.helpout.net.RetrofitClientLogin
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.StringUtils
import com.gkzxhn.helpout.utils.logE
import kotlinx.android.synthetic.main.user_fragment.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
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
        getLawyersState()

        /****** 接受控件小红点的消息 ******/
        RxBus.instance.toObserverable(RxBusBean.HomeTopRedPoint::class.java)
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    getAccountInfo()
                    getLawyersState()
                }, {
                    it.message.toString().logE(this)
                })
    }

    override fun initListener() {
        v_user_my_money_bg.setOnClickListener(this)
        v_user_rz_bg.setOnClickListener(this)
        v_user_idea_submit_bg.setOnClickListener(this)
        v_user_set_bg.setOnClickListener(this)
        v_user_top_bg.setOnClickListener(this)
        v_user_edit_info_bg.setOnClickListener(this)
        v_user_bill_bg.setOnClickListener(this)
        /****** 给个人信息栏设置背影触摸变化 ******/
        ProjectUtils.addViewTouchChange(v_user_top_bg)

    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.v_user_edit_info_bg -> {
                val intent = Intent(context, UserSettingActivity::class.java)
                intent.putExtra("name", if (accountInfo != null) accountInfo?.nickname else "")
                intent.putExtra("phoneNumber", if (accountInfo != null) accountInfo?.phoneNumber else "")
                context?.startActivity(intent)
            }
            R.id.v_user_my_money_bg -> {
                context?.startActivity(Intent(context, BountyActivity::class.java))
            }
            R.id.v_user_rz_bg -> {
                context?.startActivity(Intent(context, ExpertActivity::class.java))
            }
//            所有订单
            R.id.v_user_idea_submit_bg -> {
                context?.startActivity(Intent(context, IdeaSubmitActivity::class.java))
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
                context?.startActivity(intent)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        init()
    }

    /****** 刷新新的token ******/
    private fun getRefreshToken(refresh_token: String) {
        context?.let {
            RetrofitClientLogin.Companion.getInstance(it)
                    .mApi?.getToken("refresh_token", refreshToken = refresh_token)
                    ?.subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<Response<ResponseBody>>(it) {
                        override fun success(t: Response<ResponseBody>) {
                            if (t.code() == 200) {
                                val string = t.body().string()
                                if (!TextUtils.isEmpty(string)) {
                                    var token: String? = null
                                    var refreshToken: String? = null
                                    try {
                                        token = JSONObject(string).getString("access_token")
                                        refreshToken = JSONObject(string).getString("refresh_token")
                                    } catch (e: Exception) {

                                    }
                                    App.EDIT.putString(Constants.SP_TOKEN, token)?.commit()
                                    App.EDIT.putString(Constants.SP_REFRESH_TOKEN, refreshToken)?.commit()
                                    getLawyersInfo()
                                }
                            }
                        }

                    })
        }
    }

    /**
     * @methodName： created by liushaoxiang on 2018/10/22 3:31 PM.
     * @description：获取律师信息
     */
    private fun getLawyersInfo() {
        context?.let {
            RetrofitClient.getInstance(it).mApi.getLawyersInfo()
                    .subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<LawyersInfo>(it) {
                        override fun success(t: LawyersInfo) {
                            App.EDIT.putString(Constants.SP_CERTIFICATIONSTATUS, t.certificationStatus)?.commit()
                            tv_user_money.text="￥"+t.rewardAmount
                        }

                    })
        }
    }

    /**
     * @methodName： created by liushaoxiang on 2019/2/26 12:00 PM.
     * @description：获取律师认证状态
     */
    private fun getLawyersState() {
        context?.let {
            RetrofitClient.getInstance(it).mApi.getLawyersState()
                    .subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<LawyersInfo>(it) {
                        override fun success(t: LawyersInfo) {
                            App.EDIT.putBoolean(Constants.SP_LAWYER_CERTIFICATION_STATUS, t.lawyer!!)?.commit()
                            loadUIByLawyerState(t.lawyer!!)
                        }

                    })
        }
    }

    /**
     * @methodName： created by liushaoxiang on 2019/2/26 1:51 PM.
     * @description：加载用户或者律师不同的UI
     */
    private fun loadUIByLawyerState(lawyer: Boolean) {
        if (lawyer) {
            /****** 律师 ******/
            getLawyersInfo()
            
            v_user_info_rz_bg.visibility=View.VISIBLE
            iv_user_info_rz.visibility=View.VISIBLE
            tv_user_info_rz.visibility=View.VISIBLE

            /****** 金额那一栏 ******/
            v_user_my_money_bg.visibility=View.VISIBLE
            iv_user_get_money_start.visibility=View.VISIBLE
            tv_user_get_money.visibility=View.VISIBLE
            tv_user_money.visibility=View.VISIBLE
            iv_user_get_money_end.visibility=View.VISIBLE
            v_user_my_money_line_bg.visibility=View.VISIBLE
            v_user_my_money.visibility=View.VISIBLE

            v_user_edit_info.visibility=View.VISIBLE

        }else{
            /****** 用户 ******/
            
            v_user_info_rz_bg.visibility=View.INVISIBLE
            iv_user_info_rz.visibility=View.INVISIBLE
            tv_user_info_rz.visibility=View.INVISIBLE

            v_user_my_money_bg.visibility=View.GONE
            iv_user_get_money_start.visibility=View.GONE
            tv_user_get_money.visibility=View.GONE
            tv_user_money.visibility=View.GONE
            iv_user_get_money_end.visibility=View.GONE
            v_user_my_money_line_bg.visibility=View.VISIBLE
            v_user_my_money.visibility=View.VISIBLE

            v_user_edit_info.visibility=View.GONE

        }

    }

    /**
     * @methodName： created by liushaoxiang on 2018/10/22 3:31 PM.
     * @description：获取我的账号明细
     */
    private fun getAccountInfo() {
        context?.let {
            RetrofitClientLogin.getInstance(it).mApi
                    ?.getAccountInfo()
                    ?.subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserverNoDialog<AccountInfo>(it) {
                        override fun success(t: AccountInfo) {
                            loadUI(t)
                            accountInfo = t
                        }
                    })
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
