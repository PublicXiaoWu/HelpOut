package com.gkzxhn.helpout.activity

import android.content.Intent
import android.os.Handler
import android.text.TextUtils
import android.view.WindowManager
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.RetrofitClientPublic
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @classname：SplashActivity
 * @author：liushaoxiang
 * @date：2018/10/22 10:28 AM
 * @description：
 */
class SplashActivity : BaseActivity() {

    override fun provideContentViewId(): Int {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return R.layout.activity_splash
    }

    override fun init() {
        if (App.SP.getString(Constants.SP_TOKEN, "")?.isNotEmpty()!!) {
            getRefreshToken(App.SP.getString(Constants.SP_REFRESH_TOKEN, ""), false)
        } else {
            handler.sendEmptyMessageDelayed(0, 1000)
        }
    }

    val handler = Handler(Handler.Callback {
        if (App.SP.getString(Constants.SP_TOKEN, "")?.isNotEmpty()!! && App.SP.getBoolean(Constants.SP_ACCOUNT_COMPLETE, true)) {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        } else {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        }
        finish()
        false
    })

    /****** 刷新新的token ******/
    private fun getRefreshToken(refresh_token: String, again: Boolean) {
        RetrofitClientPublic.Companion.getInstance(this)
                .mApi.getToken("refresh_token", refreshToken = refresh_token)
                .subscribeOn(Schedulers.io())
                ?.unsubscribeOn(AndroidSchedulers.mainThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<Response<ResponseBody>>(this) {
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
                                handler.sendEmptyMessageDelayed(0, 1000)
                            }
                        } else if (t.code() == 401) {
                            if (again) {
                                handler.sendEmptyMessageDelayed(0, 1000)
                            } else {
                                App.EDIT.putString(Constants.SP_TOKEN, "").commit()
                                getRefreshToken(App.SP.getString(Constants.SP_REFRESH_TOKEN, ""), true)
                            }
                        } else {
                            handler.sendEmptyMessageDelayed(0, 1000)
                        }
                    }

                    override fun onError(t: Throwable?) {
                        super.onError(t)
                        handler.sendEmptyMessageDelayed(0, 1000)
                    }
                })
    }

}