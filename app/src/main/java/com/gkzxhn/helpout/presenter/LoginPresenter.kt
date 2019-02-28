package com.gkzxhn.helpout.presenter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.AccountInfoUpActivity
import com.gkzxhn.helpout.activity.LoginActivity
import com.gkzxhn.helpout.activity.MainActivity
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.entity.AccountInfo
import com.gkzxhn.helpout.entity.ImInfo
import com.gkzxhn.helpout.model.ILoginModel
import com.gkzxhn.helpout.model.iml.LoginModel
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.HttpObserverNoDialog
import com.gkzxhn.helpout.net.error_exception.ApiException
import com.gkzxhn.helpout.utils.*
import com.gkzxhn.helpout.view.LoginView
import com.google.gson.Gson
import com.netease.nim.uikit.api.NimUIKit
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.LoginInfo
import kotlinx.android.synthetic.main.dialog_ts.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import java.io.IOException
import java.net.ConnectException
import java.util.*

/**
 * Explanation: 登录
 * @author LSX
 *    -----2018/1/22
 */

class LoginPresenter(context: Context, view: LoginView) : BasePresenter<ILoginModel, LoginView>(context, LoginModel(), view) {

    fun login() {
        if (mView?.getCode()?.isEmpty()!! || mView?.getPhone()?.isEmpty()!!) {
            mContext?.showToast("请填写完成后操作！")
        } else if (!StringUtils.isMobileNO(mView?.getPhone()!!)) {
            mContext?.showToast("手机号格式不正确")
        } else {
            requestLogin()
        }
    }

    fun sendCode() {
        if (!StringUtils.isMobileNO(mView?.getPhone()!!)) {
            mContext?.showToast("手机号格式不正确")
        } else if (!NetworkUtils.isNetConneted(mContext!!)) {
            mContext?.showToast("暂无网络")

        } else {
            mContext?.let {
                mModel.getCode(it, mView?.getPhone()!!)
                        .unsubscribeOn(AndroidSchedulers.mainThread())
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.subscribe(object : HttpObserver<Response<Void>>(it) {
                            override fun success(t: Response<Void>) {
                                if (t.code() == 201) {
                                    mView?.startCountDown(60)
                                    it.showToast(it.getString(R.string.have_send).toString())
                                }else{
                                    it.showToast("服务器异常，请重试")
                                }
                            }

                            override fun onError(t: Throwable?) {
                                loadDialog?.dismiss()
                                mView?.stopCountDown()
                            }
                        })
            }

        }

    }

    /**
     * @methodName： created by liushaoxiang on 2018/10/19 11:50 AM.
     * @description：登录
     */
    private fun requestLogin() {
        var map = LinkedHashMap<String, String>()
        map["phoneNumber"] = mView?.getPhone().toString()
        map["verificationCode"] = mView?.getCode().toString()
        map["name"] = mView?.getPhone().toString()
        map["group"] = "CUSTOMER"
        var body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                Gson().toJson(map))
        mModel.login(mContext!!, body)
                .unsubscribeOn(AndroidSchedulers.mainThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<Response<Void>>(mContext!!) {
                    override fun success(t: Response<Void>) {
                        when (t.code()) {
                            201 -> {
                                //注册成功 sms.verification-code.NotMatched user.Existed
                                getToken(map["phoneNumber"].toString(), map["verificationCode"].toString())
                                rememberPhone()
                            }
                            400 -> {
                                try {
                                    val errorBody = t.errorBody().string()
                                    when (JSONObject(errorBody).getString("code")) {
                                        "sms.verification-code.NotMatched" -> {
                                            mContext?.TsDialog(mContext?.getString(R.string.verify_number_error).toString(), false)
                                        }
                                        "user.Existed" -> {
                                            getToken(map["phoneNumber"].toString(), map["verificationCode"].toString())
                                            rememberPhone()
                                        }
                                    //user.password.NotMatched=账号密码不匹配。
                                        "user.password.NotMatched" -> {
                                            mContext?.TsDialog(mContext?.getString(R.string.password_error).toString(), false)
                                        }
                                        "user.group.NotMatched" -> {
                                            mContext?.TsDialog(mContext?.getString(R.string.group_error).toString(), false)
                                        }
                                        "invalid_grant" -> {
                                            mContext?.TsDialog(mContext?.getString(R.string.group_error_disable).toString(), false)
                                        }
                                        else -> {

                                        }
                                    }
                                } catch (e: Exception) {
                                }
                            }
                            else -> {

                            }
                        }
                    }
                }
                )
    }

    /****** 记住账号 ******/
    private fun rememberPhone() {
        if (mView?.getRememberState()!!) {
            App.EDIT.putString(Constants.SP_REMEMBER_PHONE, mView?.getPhone()).commit()
        } else {
            App.EDIT.putString(Constants.SP_REMEMBER_PHONE, "").commit()
        }
    }


    /**
     * @methodName： created by liushaoxiang on 2018/10/19 11:51 AM.
     * @description：获取Token
     */
    private fun getToken(phoneNumber: String, code: String) {
        mContext?.let {
            mModel.getToken(it, phoneNumber, code)?.observeOn(AndroidSchedulers.mainThread())
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
                                    getImInfo()
                                }
                            } else if (t.code() == 400) {
                                when (JSONObject(t.errorBody().string()).getString("error")) {
                                    "user.password.NotMatched" -> {
                                        mContext?.TsDialog(mContext?.getString(R.string.password_error).toString(), false)
                                    }
                                    "user.group.NotMatched" -> {
                                        mContext?.TsDialog(mContext?.getString(R.string.group_error).toString(), false)
                                    }
                                    "invalid_grant" -> {
                                        mContext?.TsDialog(mContext?.getString(R.string.group_error_disable).toString(), false)
                                    }
                                    else -> {

                                    }
                                }
                            }

                        }

                        override fun onError(e: Throwable?) {
                            loadDialog?.dismiss()
                            when (e) {
                                is ConnectException -> mContext?.TsDialog("服务器异常，请重试", false)
                                is HttpException -> {
                                    if (e.code() == 401) {
                                        mContext?.TsClickDialog("登录已过期", false)?.dialog_save?.setOnClickListener {
                                            App.EDIT.putString(Constants.SP_TOKEN, "")?.commit()
                                            val intent = Intent(mContext, LoginActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            mContext?.startActivity(intent)
                                        }
                                    } else {
                                        mContext?.TsDialog("服务器异常，请重试", false)
                                    }
                                }
                                is IOException -> mContext?.TsDialog("数据加载失败，请检查您的网络", false)
                            //后台返回的message
                                is ApiException -> {
                                    mContext?.TsDialog(e.message!!, false)
                                    Log.e("ApiErrorHelper", e.message, e)
                                }
                                else -> {
                                    mContext?.showToast("数据异常")
                                    Log.e("ApiErrorHelper", e?.message, e)
                                }
                            }
                        }
                    })
        }
    }

    /**
     * @methodName： created by liushaoxiang on 2018/10/22 3:31 PM.
     * @description：获取我的账号明细
     */
    private fun getAccountInfo() {
        mContext?.let {
            mModel.getAccountInfo(it)
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserverNoDialog<AccountInfo>(mContext!!) {
                        override fun success(t: AccountInfo) {
                            if (t.nickname.isNullOrEmpty()||t.avatar.isNullOrEmpty()) {
                                val intent = Intent(mContext, AccountInfoUpActivity::class.java)
                                intent.putExtra("name",t.username)
                                intent.putExtra("phoneNumber",t.phoneNumber)
                                mContext?.startActivity(intent)
                                mView?.onFinish()
                            } else {
                                mContext?.startActivity(Intent(mContext, MainActivity::class.java))
                                mView?.onFinish()
                            }
                        }

                        override fun onError(t: Throwable?) {
                        }
                    })
        }
    }

    /**
     * @methodName： created by liushaoxiang on 2018/10/22 3:31 PM.
     * @description：获取网易信息
     */
    private fun getImInfo() {
        mContext?.let {
            mModel.getIMInfo(it)
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserverNoDialog<ImInfo>(mContext!!) {
                        override fun success(t: ImInfo) {
                            loginNim(t.account!!, t.token!!)
                        }

                        override fun onError(t: Throwable?) {
                        }
                    })
        }
    }

    /**
     * 登录云信
     * @param account
     * @param pwd
     */
    private fun loginNim(account: String, pwd: String) {
        val loginInfo = LoginInfo(account, pwd)
        NIMClient.getService(AuthService::class.java).login(loginInfo).setCallback(object : RequestCallback<LoginInfo> {
            override fun onSuccess(param: LoginInfo) {
                App.EDIT.putString(Constants.SP_IM_ACCOUNT, param.account).commit()
                App.EDIT.putString(Constants.SP_IM_TOKEN, param.token).commit()
                NimUIKit.setAccount(account)
                getAccountInfo()
            }

            override fun onFailed(code: Int) {
            }

            override fun onException(exception: Throwable) {
            }
        })
    }

}