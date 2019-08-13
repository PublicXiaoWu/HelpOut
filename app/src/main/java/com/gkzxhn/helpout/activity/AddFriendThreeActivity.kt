package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.google.gson.Gson
import com.netease.nim.uikit.common.ToastHelper
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.friend.constant.VerifyType
import com.netease.nimlib.sdk.friend.model.AddFriendData
import kotlinx.android.synthetic.main.activity_add_friend_three.*
import kotlinx.android.synthetic.main.default_top.*

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/19 9:14 AM
 * @description  添加好友 第三页 发起好友验证信息
 */
class AddFriendThreeActivity : BaseActivity() {

    var account = ""
    var curUserName = ""
    override fun provideContentViewId(): Int {
        return R.layout.activity_add_friend_three
    }

    @SuppressLint("SetTextI18n")
    override fun init() {
        initTopTitle()

        account = intent.getStringExtra("account")
        curUserName = intent.getStringExtra("curUserName")

        et_add_friend_three_content.setText(getString(R.string.i_is) + App.SP.getString(Constants.SP_NAME, ""))
        iv_add_friend_three_close_text.setOnClickListener {
            et_add_friend_three_content.setText("")
        }


        /****** 弹出键盘并定位焦点到字的后面   配置文件中也配置了自动弹出键盘******/
        et_add_friend_three_content.setSelection(et_add_friend_three_content.text.length)

    }


    private fun initTopTitle() {
        tv_default_top_title.text = getString(R.string.friend_verification)
        iv_default_top_back.setOnClickListener {
            finish()
        }
        tv_default_top_end.visibility = View.VISIBLE
        tv_default_top_end.text = getString(R.string.send)
        tv_default_top_end.setOnClickListener {
            /****** 如果键盘没有关掉 执行关掉代码 ******/
            val inputManger = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManger.hideSoftInputFromWindow(window.peekDecorView().windowToken, 0)
            addFriend()
        }
    }

    private fun addFriend() {
        val content = et_add_friend_three_content.text.toString().trim()
        val map = LinkedHashMap<String, String>()
        map["verifyContent"] = content
        map["nickName"] = App.SP.getString(Constants.SP_NAME, "")
        map["curUserName"] = curUserName
        val json = Gson().toJson(map)
        NIMClient.getService(FriendService::class.java).addFriend(AddFriendData(account, VerifyType.VERIFY_REQUEST, json))
                .setCallback(object : RequestCallback<Void> {
                    override fun onSuccess(p0: Void?) {
                        ToastHelper.showToast(this@AddFriendThreeActivity,  getString(R.string.add_friend_success))
                        finish()
                    }

                    override fun onFailed(code: Int) {
                        if (code == 408) {
                            ToastHelper.showToast(this@AddFriendThreeActivity, R.string.network_is_not_available)
                        } else {
                            ToastHelper.showToast(this@AddFriendThreeActivity, "on failed:$code")
                        }
                    }

                    override fun onException(p0: Throwable?) {
                        ToastHelper.showToast(this@AddFriendThreeActivity, getString(R.string.add_friend_fail))

                    }

                })
    }
}

