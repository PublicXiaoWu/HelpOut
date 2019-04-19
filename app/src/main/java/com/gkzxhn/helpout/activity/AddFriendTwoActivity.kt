package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.content.Intent
import com.gkzxhn.helpout.R
import kotlinx.android.synthetic.main.activity_add_friend_two.*
import kotlinx.android.synthetic.main.default_top.*

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/19 9:14 AM
 * @description  添加好友 第二页 好友信息显示
 */
class AddFriendTwoActivity : BaseActivity() {
    override fun provideContentViewId(): Int {
        return R.layout.activity_add_friend_two
    }

    @SuppressLint("SetTextI18n")
    override fun init() {
        initTopTitle()

        tv_add_friend_two_add.setOnClickListener {
            startActivity(Intent(this, AddFriendThreeActivity::class.java))
            finish()
        }
    }


    private fun initTopTitle() {
        tv_default_top_title.text = "好友信息"
        iv_default_top_back.setOnClickListener {
            finish()
        }

    }
}

