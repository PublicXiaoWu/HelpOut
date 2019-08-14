package com.gkzxhn.helpout.presenter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.ChangePhoneSecondActivity
import com.gkzxhn.helpout.activity.LoginActivity
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.model.IPhoneChangeModel
import com.gkzxhn.helpout.model.iml.PhoneChangeModel
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.error_exception.NetCodeHelper
import com.gkzxhn.helpout.utils.StringUtils
import com.gkzxhn.helpout.utils.TsClickDialog
import com.gkzxhn.helpout.utils.TsDialog
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.view.PhoneChangeView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dialog_ts.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import rx.android.schedulers.AndroidSchedulers
import java.util.*

/**
 * Explanation: 更换电话号码
 * @author LSX
 *    -----2018/1/22
 */

class PhoneChangePresenter(context: Context, view: PhoneChangeView) : BasePresenter<IPhoneChangeModel, PhoneChangeView>(context, PhoneChangeModel(), view) {

    fun login() {
        if (mView?.getCode()?.isEmpty()!!) {
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
            return
        }
        mContext?.let {
            val map = LinkedHashMap<String, String>()
            map["phoneNumber"] = mView?.getPhone().toString()
            val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                    Gson().toJson(map))
            mModel.getCode(it, body)
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<Response<Void>>(it) {
                        override fun success(t: Response<Void>) {
                            mView?.startCountDown(60)
                            it.showToast(it.getString(R.string.have_send).toString())
                        }

                        override fun onError(t: Throwable?) {
                            mView?.stopCountDown()

                        }
                    })
        }

    }

    /**
     * @methodName： created by liushaoxiang on 2018/10/19 11:50 AM.
     * @description：登录
     */
    private fun requestLogin() {
        val map = LinkedHashMap<String, String>()
        map["phoneNumber"] = mView?.getPhone().toString()
        map["verificationCode"] = mView?.getCode().toString()
        map["name"] = mView?.getPhone().toString()
        map["group"] = "CUSTOMER"
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                Gson().toJson(map))
        mModel.login(mContext!!, body)
                .unsubscribeOn(AndroidSchedulers.mainThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<Response<Void>>(mContext!!) {
                    override fun success(t: Response<Void>) {
                        when (t.code()) {
                            201 -> {
                                //注册成功 sms.verification-code.NotMatched user.Existed
                            }
                            400 -> {
                                try {
                                    val errorBody = t.errorBody()?.string()
                                    when (JSONObject(errorBody).getString("code")) {
                                        "sms.verification-code.NotMatched" -> {
                                            mContext?.TsDialog(mContext?.getString(R.string.verify_number_error).toString(), false)

                                        }
                                        "user.Existed" -> {
                                            getToken(mView?.getPhone()!!, mView?.getCode()!!)
                                        }
                                        //user.password.NotMatched=账号密码不匹配。
                                        "user.password.NotMatched" -> {
                                            mContext?.TsDialog(mContext?.getString(R.string.password_error).toString(), false)

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

    /**
     * @methodName： created by liushaoxiang on 2018/10/19 11:51 AM.
     * @description：获取Token
     */
    private fun getToken(phoneNumber: String, code: String) {
        mContext?.let {
            mModel.getToken(it, phoneNumber, code)?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<Response<ResponseBody>>(it) {
                        override fun success(t: Response<ResponseBody>) {
                            when (t.code()) {
                                200 -> {
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
                                        /****** 已存在账号  表示验证通过  跳转下一步 ******/
                                        mContext?.startActivity(Intent(mContext, ChangePhoneSecondActivity::class.java))
                                        mView?.onFinish()
                                    }
                                }
                                400 -> {
                                    NetCodeHelper.handleCommonCode(it,t.errorBody()!!.string())
                                }
                                401 -> {
                                    mContext?.TsClickDialog("登录已过期", false)?.dialog_save?.setOnClickListener {
                                        App.EDIT.putString(Constants.SP_TOKEN, "")?.commit()
                                        val intent = Intent(mContext, LoginActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        mContext?.startActivity(intent)
                                    }
                                }
                                else -> {
                                    mContext?.showToast("服务器异常")

                                }
                            }

                        }

                    })
        }
    }

    /**
     * @methodName： created by liushaoxiang on 2018/10/29 10:57 AM.
     * @description：修改手机号
     */
    fun updatePhoneNumber() {
        if (mView?.getCode()?.isEmpty()!!) {
            mContext?.showToast("请填写完成后操作！")
            return
        } else if (!StringUtils.isMobileNO(mView?.getPhone()!!)) {
            mContext?.showToast("手机号格式不正确")
            return
        }
        val map = LinkedHashMap<String, String>()
        map["phoneNumber"] = mView?.getPhone().toString()
        map["verificationCode"] = mView?.getCode().toString()
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                Gson().toJson(map))
        mModel.updatePhoneNumber(mContext!!, body)
                .unsubscribeOn(AndroidSchedulers.mainThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<Response<Void>>(mContext!!) {
                    override fun success(t: Response<Void>) {
                        when (t.code()) {
                            204 -> {
                                mContext?.showToast("更换成功")
                                mView?.onFinish()
                            }
                            400 -> {
                                val errorBody = t.errorBody()?.string()
                                when (JSONObject(errorBody).getString("code")) {
                                    "user.Existed" ->
                                        mContext?.showToast(JSONObject(errorBody).getString("message").toString())
                                    "sms.verification-code.NotMatched" ->
                                        mContext?.showToast(JSONObject(errorBody).getString("message").toString())
                                    else ->
                                        mContext?.showToast(JSONObject(errorBody).getString("message").toString())
                                }
                            }
                            else -> {
                                mContext?.showToast(t.code().toString() + t.errorBody())
                            }
                        }
                    }

                }
                )
    }

}