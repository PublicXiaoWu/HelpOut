package com.gkzxhn.helpout.activity

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.gkzxhn.helpout.R
import com.netease.nim.uikit.common.ToastHelper
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.friend.constant.VerifyType
import com.netease.nimlib.sdk.friend.model.AddFriendData
import kotlinx.android.synthetic.main.activity_add_friend.*


/**
 * @classname：AddFriendActivity
 * @author：liushaoxiang
 * @date：2019/4/15 2:43 PM
 * @description：添加朋友
 */
class AddFriendActivity : BaseActivity() {


    override fun init() {

        /****** 弹出搜索框 ******/
        tv_add_friend_phone.setOnClickListener {
            cl_add_friend_search.visibility = View.VISIBLE
            tv_add_friend_phone.visibility = View.GONE
            iv_add_friend_phone_start.visibility = View.GONE

            et_add_friend.setText("")
            /****** 弹出键盘 ******/
            et_add_friend.requestFocus()
            val inputManger = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManger.showSoftInput(et_add_friend, 0)

        }

        /****** 取消 ******/
        tv_add_friend_cancel.setOnClickListener {
            cancel()
        }
        /****** 返回 ******/
        iv_add_friend_back.setOnClickListener {
            finish()
        }

        /****** 搜索 ******/
        v_add_friend_item.setOnClickListener {
            goSearch()
        }

        /******  输入框监听 ******/
        et_add_friend.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val phone = et_add_friend.text.toString().trim()
                if (phone.isEmpty()) {
                    cl_add_friend_item.visibility = View.GONE
                } else {
                    cl_add_friend_item.visibility = View.VISIBLE
                    tv_add_friend_no_friend.visibility = View.GONE
                    tv_add_friend_item_context.text = phone
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        /****** 回车监听 ******/
        et_add_friend.setOnKeyListener { v, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                goSearch()
                true;
            } else
                false
        }
    }

    private fun goSearch() {
        cl_add_friend_item.visibility = View.GONE
        tv_add_friend_no_friend.visibility = View.VISIBLE
        NIMClient.getService(FriendService::class.java).addFriend(AddFriendData("qwerty", VerifyType.VERIFY_REQUEST, "来啊"))
                .setCallback(object : RequestCallback<Void> {
                    override fun onSuccess(param: Void) {
                        ToastHelper.showToast(this@AddFriendActivity, "添加好友请求发送成功")
                    }
                    override fun onFailed(code: Int) {
                        if (code == 408) {
                            ToastHelper.showToast(this@AddFriendActivity, R.string.network_is_not_available)
                        } else {
                            ToastHelper.showToast(this@AddFriendActivity, "on failed:$code")
                        }
                    }

                    override fun onException(exception: Throwable) {
                        ToastHelper.showToast(this@AddFriendActivity, "添加好友请求发送失败")

                    }
                })


    }

    /****** 取消按扭 ******/
    private fun cancel() {
        cl_add_friend_search.visibility = View.GONE
        cl_add_friend_item.visibility = View.GONE
        tv_add_friend_no_friend.visibility = View.GONE

        tv_add_friend_phone.visibility = View.VISIBLE
        iv_add_friend_phone_start.visibility = View.VISIBLE


        /****** 如果键盘没有关掉 执行关掉代码 ******/
        val view = window.peekDecorView()
        if (view != null) {
            val inputManger = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManger.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun provideContentViewId(): Int {
        return R.layout.activity_add_friend
    }


    override fun onBackPressed() {
        if (cl_add_friend_search.visibility == View.VISIBLE) {
            cancel()
        } else {
            super.onBackPressed()
        }
    }


}