package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.content.Intent
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.StringUtils
import com.gkzxhn.helpout.utils.TsDialog
import kotlinx.android.synthetic.main.activity_add_friend_two.*
import kotlinx.android.synthetic.main.default_top.*

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/19 9:14 AM
 * @description  添加好友 第二页 好友信息显示
 */
class AddFriendTwoActivity : BaseActivity() {

    var account = ""
    var phoneNumber = ""
    override fun provideContentViewId(): Int {
        return R.layout.activity_add_friend_two
    }

    @SuppressLint("SetTextI18n")
    override fun init() {
        initTopTitle()

        phoneNumber = intent.getStringExtra("phoneNumber")
        account = intent.getStringExtra("account")
        val nickname = intent.getStringExtra("nickname")
        val curUserName = intent.getStringExtra("curUserName")
        val username = intent.getStringExtra("username")

        tv_add_friend_two_add.setOnClickListener {
            if (phoneNumber == App.SP.getString(Constants.SP_PHONE, "")) {
                TsDialog(getString(R.string.not_add_my_phone), true)
                return@setOnClickListener
            }
            val intent = Intent(this, AddFriendThreeActivity::class.java)
            intent.putExtra("account", account)
            intent.putExtra("curUserName", curUserName)
            startActivity(intent)
        }

        /****** 头像列的点击事件 ******/
        v_add_friend_two_top_bg.setOnClickListener {
        }

        if (!username.isNullOrEmpty()) {
            ProjectUtils.loadRoundImageByUserName(this, username, iv_add_friend_two)
        }

        tv_add_friend_two_name.text = nickname
        tv_add_friend_two_phone.text = StringUtils.phoneChange(phoneNumber)
    }

    private fun initTopTitle() {
        tv_default_top_title.text = getString(R.string.friend_Info)
        iv_default_top_back.setOnClickListener {
            finish()
        }

    }
}

