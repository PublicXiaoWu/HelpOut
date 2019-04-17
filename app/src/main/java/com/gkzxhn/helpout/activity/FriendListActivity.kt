package com.gkzxhn.helpout.activity

import android.content.Intent
import com.gkzxhn.helpout.R
import com.netease.nim.uikit.business.contact.ContactsFragment
import kotlinx.android.synthetic.main.activity_friend_list.*
import kotlinx.android.synthetic.main.default_top.*

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/16 10:13 AM
 * @description：通讯录
 */
class FriendListActivity : BaseActivity() {

    override fun provideContentViewId(): Int {
        return R.layout.activity_friend_list
    }

    override fun init() {
        initTitle()

        val beginTransaction = supportFragmentManager.beginTransaction()
        //提交事务
        beginTransaction.add(R.id.fl_friend_list, ContactsFragment()).commit();

        ll_add_friend.setOnClickListener {
            startActivity(Intent(this, NewFriendActivity::class.java))
        }
    }

    private fun initTitle() {
        tv_default_top_title.text = "通讯录"
        iv_default_top_back.setOnClickListener { finish() }
    }

}