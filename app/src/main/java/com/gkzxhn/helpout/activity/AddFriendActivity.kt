package com.gkzxhn.helpout.activity

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.gkzxhn.helpout.R
import com.netease.nim.uikit.common.ToastHelper
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.friend.constant.VerifyType
import com.netease.nimlib.sdk.friend.model.AddFriendData
import com.netease.nimlib.sdk.friend.model.AddFriendNotify
import com.netease.nimlib.sdk.msg.SystemMessageObserver
import com.netease.nimlib.sdk.msg.SystemMessageService
import com.netease.nimlib.sdk.msg.constant.SystemMessageType
import com.netease.nimlib.sdk.msg.model.SystemMessage
import kotlinx.android.synthetic.main.activity_add_friend.*


/**
 * @classname：AddFriendActivity
 * @author：liushaoxiang
 * @date：2019/4/15 2:43 PM
 * @description：添加朋友
 */
class AddFriendActivity : BaseActivity() {

    private val systemMessageObserver = Observer<SystemMessage> { systemMessage ->
        if (systemMessage.type === SystemMessageType.AddFriend) {
            val attachData = systemMessage.attachObject as AddFriendNotify
            when(attachData.event) {
                 AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT -> {
                    Log.e("xiaowu_add","对方直接添加你为好友")
                    // 对方直接添加你为好友
                }
                 AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND -> {
                    Log.e("xiaowu_add","对方通过了你的好友验证请求")
                    // 对方通过了你的好友验证请求
                }
                 AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND -> {
                    Log.e("xiaowu_add","对方拒绝了你的好友验证请求")
                    // 对方拒绝了你的好友验证请求
                }
                 AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST -> {
                    Log.e("xiaowu_add","对方请求添加好友，一般场景会让用户选择同意或拒绝对方的好友请求。"+attachData.account+attachData.msg)
                    // 对方请求添加好友，一般场景会让用户选择同意或拒绝对方的好友请求。
                    // 通过message.getContent()获取好友验证请求的附言
                }
            }
        }
    }


    override fun init() {

        NIMClient.getService<SystemMessageObserver>(SystemMessageObserver::class.java).observeReceiveSystemMsg(systemMessageObserver, true)


        val temps = NIMClient.getService(SystemMessageService::class.java).querySystemMessagesBlock(0, 100)


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
//        9f9469948f76456d850b9f3bed1ddc10 肖君
//        f8ffb4a93b084dd2b9ba66e932f1c1ff
        NIMClient.getService(FriendService::class.java).addFriend(AddFriendData("9f9469948f76456d850b9f3bed1ddc10", VerifyType.VERIFY_REQUEST, "肖君同学发车了"))
                .setCallback(object : RequestCallback<Void> {
                    override fun onSuccess(p0: Void?) {
                        ToastHelper.showToast(this@AddFriendActivity, "添加好友请求发送成功")
                    }

                    override fun onFailed(code: Int) {
                        if (code == 408) {
                            ToastHelper.showToast(this@AddFriendActivity, R.string.network_is_not_available)
                        } else {
                            ToastHelper.showToast(this@AddFriendActivity, "on failed:$code")
                        }
                    }

                    override fun onException(p0: Throwable?) {
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