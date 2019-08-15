package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.view.WindowManager
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.presenter.LoginPresenter
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.view.LoginView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit
import kotlinx.android.synthetic.main.activity_login.et_login_code as code
import kotlinx.android.synthetic.main.activity_login.et_login_phone as loginPhone
import kotlinx.android.synthetic.main.activity_login.tv_login_get_code as sendCode
import kotlinx.android.synthetic.main.activity_login.tv_login_login as login

/**
 * Explanation: 登录页
 * @author LSX
 *    -----2018/9/6
 */

class LoginActivity : BaseActivity(), LoginView {

    override fun onFinish() {
        finish()
    }


    var timeDisposable: Disposable? = null      //倒计时任务
    private var sendClick: Boolean = false  //是否已经点击发送验证码


    lateinit var mPresenter: LoginPresenter

    override fun getPhone(): String {
        return loginPhone.text.toString().trim()
    }

    override fun getCode(): String {
        return code.text.toString().trim()
    }

    override fun getRememberState(): Boolean {
        return cb_remember.isChecked
    }


    override fun provideContentViewId(): Int {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return R.layout.activity_login
    }


    override fun init() {
        mPresenter = LoginPresenter(this, this)

        val phone = App.SP.getString(Constants.SP_REMEMBER_PHONE, "")

        if (phone.isNotEmpty()) {
            loginPhone.setText(phone)
            /****** 获取光标到验证码一栏 ******/
            code.requestFocus()
        }

        mPresenter.updateApp()
        login.setOnClickListener {
            mPresenter.login()
        }

        sendCode.setOnClickListener {
            mPresenter.sendCode()
        }
        ProjectUtils.addViewTouchChange(login)
    }

    /**
     * 开始倒计时
     */
    @SuppressLint("SetTextI18n")
    override fun startCountDown(seconds: Int) {
        sendClick = true
        timeDisposable = Observable.interval(0, 1L, TimeUnit.SECONDS)
                .take(seconds + 1L)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    runOnUiThread {
                        if (it == 60L) {
                            sendCode.text = resources.getString(R.string.get_verify)
                            sendCode.setTextColor(resources.getColor(R.color.dark_blue))
                            sendCode.isClickable = true
                        } else {
                            sendCode.text = "${60L - it}s"
                            sendCode.setTextColor(resources.getColor(R.color.text_gray))
                            sendCode.isClickable = false

                        }
                    }
                }, {
                })
    }

    /**
     * 停止倒计时
     */
    override fun stopCountDown() {
        sendClick = false
        if (timeDisposable != null) {
            if (!timeDisposable!!.isDisposed) {
                timeDisposable!!.dispose()
            }
        }
        sendCode.isClickable = true
        sendCode.text = getString(R.string.get_verify)
        sendCode.setTextColor(resources.getColor(R.color.dark_blue))
    }

}
