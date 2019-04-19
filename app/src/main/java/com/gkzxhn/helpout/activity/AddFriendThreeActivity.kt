package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
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

    var account=""
    override fun provideContentViewId(): Int {
        return R.layout.activity_add_friend_three
    }

    @SuppressLint("SetTextI18n")
    override fun init() {
        initTopTitle()

        account=intent.getStringExtra("account")

        et_add_friend_three_content.setText("我是" + App.SP.getString(Constants.SP_NAME, ""))
        iv_add_friend_three_close_text.setOnClickListener {
            et_add_friend_three_content.setText("")
        }


        /****** 弹出键盘并定位焦点到字的后面 ******/
        et_add_friend_three_content.setSelection(et_add_friend_three_content.text.length)
//        val inputManger = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputManger.showSoftInput(et_add_friend_three_content, 0)

    }


    private fun initTopTitle() {
        tv_default_top_title.text = "朋友验证"
        iv_default_top_back.setOnClickListener {
            finish()
        }
        tv_default_top_end.visibility = View.VISIBLE
        tv_default_top_end.text = "发送"
        tv_default_top_end.setOnClickListener {
            addFriend()
        }
    }

    private fun addFriend() {

        //9f9469948f76456d850b9f3bed1ddc10 肖君
        // f8ffb4a93b084dd2b9ba66e932f1c1ff

        NIMClient.getService(FriendService::class.java).addFriend(AddFriendData(account, VerifyType.VERIFY_REQUEST, et_add_friend_three_content.text.toString().trim()))
                .setCallback(object : RequestCallback<Void> {
                    override fun onSuccess(p0: Void?) {
                        ToastHelper.showToast(this@AddFriendThreeActivity, "添加好友请求发送成功")
                    }

                    override fun onFailed(code: Int) {
                        if (code == 408) {
                            ToastHelper.showToast(this@AddFriendThreeActivity, R.string.network_is_not_available)
                        } else {
                            ToastHelper.showToast(this@AddFriendThreeActivity, "on failed:$code")
                        }
                    }

                    override fun onException(p0: Throwable?) {
                        ToastHelper.showToast(this@AddFriendThreeActivity, "添加好友请求发送失败")

                    }

                })
    }
}

