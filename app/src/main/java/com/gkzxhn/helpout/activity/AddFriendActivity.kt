package com.gkzxhn.helpout.activity

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.entity.ImInfo
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.RetrofitClientChat
import com.gkzxhn.helpout.utils.showToast
import kotlinx.android.synthetic.main.activity_add_friend.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


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
        val phoneNumber = et_add_friend.text.toString().trim()
        if (phoneNumber.isEmpty()) {
            showToast("请输入手机号")
            return
        }

        mCompositeSubscription.add(RetrofitClientChat
                .getInstance(this).mApi.getUserIm(phoneNumber)
                .subscribeOn(Schedulers.io())
                ?.unsubscribeOn(AndroidSchedulers.mainThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<ImInfo>(this) {
                    override fun success(t: ImInfo) {

                        val intent = Intent(this@AddFriendActivity, AddFriendTwoActivity::class.java)
                        intent.putExtra("phoneNumber",phoneNumber)
                        intent.putExtra("account",t.account)
                        intent.putExtra("nickname",t.nickname)
                        intent.putExtra("avatar",t.avatar)
                        intent.putExtra("curUserName",t.curUserName)
                        startActivity(intent)
                    }
                }))
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


    /****** 好友不存在 ******/
    private fun notFindFriend() {
        cl_add_friend_item.visibility = View.GONE
        tv_add_friend_no_friend.visibility = View.VISIBLE
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