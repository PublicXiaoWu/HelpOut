package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.utils.StringUtils
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
    override fun provideContentViewId(): Int {
        return R.layout.activity_add_friend_two
    }

    @SuppressLint("SetTextI18n")
    override fun init() {
        initTopTitle()

        val phoneNumber = intent.getStringExtra("phoneNumber")
        account = intent.getStringExtra("account")
        val nickname = intent.getStringExtra("nickname")
        val avatar = intent.getStringExtra("avatar")


        tv_add_friend_two_add.setOnClickListener {
            val intent = Intent(this, AddFriendThreeActivity::class.java)
            intent.putExtra("account", account)
            startActivity(intent)
            finish()
        }

        v_add_friend_two_top_bg.setOnClickListener {
            startActivity(Intent(this, FriendInfoActivity::class.java))
        }

        if (!avatar.isNullOrEmpty()) {
            Glide.with(this).load(avatar)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.circleCropTransform())
                    .into(iv_add_friend_two)
        }

        tv_add_friend_two_name.text = nickname
        tv_add_friend_two_phone.text = StringUtils.phoneChange(phoneNumber)
    }


    private fun initTopTitle() {
        tv_default_top_title.text = "好友信息"
        iv_default_top_back.setOnClickListener {
            finish()
        }

    }
}

