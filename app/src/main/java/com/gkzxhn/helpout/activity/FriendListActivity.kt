package com.gkzxhn.helpout.activity

import com.gkzxhn.helpout.R
import com.netease.nim.uikit.business.contact.ContactsFragment
import kotlinx.android.synthetic.main.default_top.*

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/16 10:13 AM
 * @description：通讯录
 */
class FriendListActivity : BaseActivity() {

    override fun provideContentViewId(): Int {
        return R.layout.activity_legalconsulting
    }

    override fun init() {
        initTitle()

        val beginTransaction = supportFragmentManager.beginTransaction()
        //提交事务
        beginTransaction.add(R.id.fl_legal_consulting, ContactsFragment()).commit();

    }

    private fun initTitle() {
        tv_default_top_title.text = "通讯录"
        iv_default_top_back.setOnClickListener { finish() }
    }

}