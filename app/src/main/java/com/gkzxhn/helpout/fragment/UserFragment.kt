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
import com.gkzxhn.helpout.entity.rxbus.RxBusBean
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.HttpObserverNoDialog
import com.gkzxhn.helpout.net.RetrofitClient
import com.gkzxhn.helpout.net.RetrofitClientPublic
import com.gkzxhn.helpout.net.error_exception.ApiException
import com.gkzxhn.helpout.utils.*
import kotlinx.android.synthetic.main.dialog_ts.*
import kotlinx.android.synthetic.main.user_fragment.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.net.ConnectException


/**
 * Explanation: 个人中心
 * @author LSX
 *    -----2018/9/7
 */

class UserFragment : BaseFragment(), View.OnClickListener {
    var accountInfo: AccountInfo? = null

    var showRedPoint = false

    override fun provideContentViewId(): Int {
        return R.layout.user_fragment
    }

    override fun init() {
        getAccountInfo()
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
        /****** 收到有新朋友的消息 ******/
        RxBus.instance.toObserverable(RxBusBean.AddPoint::class.java)
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showRedPoint = it.show
                    v_user_friend_list_point.visibility = if (it.show) View.VISIBLE else View.GONE
                }, {
                    it.message.toString().logE(this)
                })
        /****** 收到有新生活圈动态的消息（点赞评论） ******/
        RxBus.instance.toObserverable(RxBusBean.MyLivesCirclePoint::class.java)
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    v_user_lives_circle_point.visibility = if (it.show) View.VISIBLE else View.GONE
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
                val intent = Intent(context, FriendListActivity::class.java)
                intent.putExtra("showRedPoint", showRedPoint)
                context?.startActivity(intent)
            }
            /****** 我的生活圈 ******/
            R.id.v_user_my_lives_circle_bg -> {
                val intent = Intent(context, LivesCircleActivity::class.java)
                intent.putExtra("LivesCircleType", 2)
                context?.startActivity(intent)

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
                    ?.subscribe(object : HttpObserverNoDialog<LawyersInfo>(it) {
                        override fun success(t: LawyersInfo) {
                            App.EDIT.putString(Constants.SP_CERTIFICATIONSTATUS, t.certificationStatus)?.commit()
                            val lawyerState = t.certificationStatus == Constants.CERTIFIED
                            loadUIByLawyerState(lawyerState)
                            if (lawyerState) {
                                tv_user_money.text = "￥" + t.rewardAmount
                            }
                        }

                        override fun onError(t: Throwable?) {
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
                    ?.subscribe(object : HttpObserverNoDialog<LawyersInfo>(it) {
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
                    .getAccountInfo()
                    .subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserverNoDialog<AccountInfo>(it) {
                        override fun success(t: AccountInfo) {
                            loadUI(t)
                            accountInfo = t
                        }

                        override fun onError(e: Throwable?) {
                            when (e) {
                                is ConnectException -> it.TsDialog(getString(R.string.abnormal_server), false)
                                is HttpException -> {
                                    when (e.code()) {
                                        401 -> {
                                            App.EDIT.putString(Constants.SP_TOKEN, "")?.commit()
                                            getRefreshToken(App.SP.getString(Constants.SP_REFRESH_TOKEN, ""))
                                        }
                                        400 -> {
                                            val errorBody = e.response().errorBody()?.string()
                                            val message = try {
                                                JSONObject(errorBody).getString("message")
                                            } catch (e: Exception) {
                                                ""
                                            }
                                            it.TsDialog(message, false)
                                        }
                                        else -> {
                                            it.TsDialog(getString(R.string.abnormal_server), false)
                                        }
                                    }
                                }
                                is IOException -> {
                                    it.showToast(getString(R.string.net_time_out))
                                }
                                //后台返回的message
                                is ApiException -> {
                                    it.TsDialog(e.message!!, false)
                                }
                                else -> {
                                    it.showToast(getString(R.string.abnormal_data_processing))
                                }
                            }
                        }
                    }))
        }
    }

    /****** 刷新新的token ******/
    private fun getRefreshToken(refresh_token: String) {
        context?.let {
            mCompositeSubscription.add(
                    RetrofitClientPublic.Companion.getInstance(it)
                            .mApi.getToken("refresh_token", refreshToken = refresh_token)
                            .subscribeOn(Schedulers.io())
                            ?.unsubscribeOn(AndroidSchedulers.mainThread())
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.subscribe(object : HttpObserver<Response<ResponseBody>>(it) {
                                override fun success(t: Response<ResponseBody>) {
                                    if (t.code() == 200) {

                                        val string = t.body()?.string()
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
                                            getAccountInfo()

                                        }
                                    } else {

                                        it.TsClickDialog(getString(R.string.login_has_expired), false).dialog_save.setOnClickListener {
                                            App.EDIT.putString(Constants.SP_TOKEN, "")?.commit()
                                            val intent = Intent(context, LoginActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            context?.startActivity(intent)
                                        }

                                    }
                                }
                            })
            )
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
        context?.let {
            ProjectUtils.loadMyIcon(it, iv_user_icon)
        }
    }

}
