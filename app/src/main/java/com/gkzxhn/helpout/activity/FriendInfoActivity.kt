package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.content.Intent
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.utils.showToast
import kotlinx.android.synthetic.main.activity_friend_info.*
import kotlinx.android.synthetic.main.default_top.*

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/19 9:14 AM
 * @description   好友信息
 */
class FriendInfoActivity : BaseActivity() {

    override fun provideContentViewId(): Int {
        return R.layout.activity_friend_info
    }

    @SuppressLint("SetTextI18n")
    override fun init() {
        initTopTitle()

        /****** 生活圈 ******/
        v_friend_info_lives_circle.setOnClickListener {
            startActivity(Intent(this, LivesCircleActivity::class.java))
            finish()
        }

        /****** 发消息 ******/
        v_friend_info_send_bg.setOnClickListener {
           showToast("发消息")
        }
    }

    private fun initTopTitle() {
        tv_default_top_title.text = "好友信息"
        iv_default_top_back.setOnClickListener {
            finish()
        }

    }
}

