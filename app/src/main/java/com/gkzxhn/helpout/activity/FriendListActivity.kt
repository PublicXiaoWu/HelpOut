package com.gkzxhn.helpout.activity

import android.content.Intent
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.entity.RxBusBean
import com.gkzxhn.helpout.utils.logE
import com.netease.nim.uikit.business.contact.ContactsFragment
import kotlinx.android.synthetic.main.activity_friend_list.*
import kotlinx.android.synthetic.main.default_top.*
import rx.android.schedulers.AndroidSchedulers

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

        val showRedPoint = intent.getBooleanExtra("showRedPoint", false)
        v_friend_list_point_number.visibility = if (showRedPoint) View.VISIBLE else View.GONE
        v_friend_list_point_number.text=newFriendNumber.toString()

        /****** 收到有新朋友的消息 ******/
        RxBus.instance.toObserverable(RxBusBean.AddPoint::class.java)
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    v_friend_list_point_number.visibility = if (it.show) View.VISIBLE else View.GONE
                }, {
                    it.message.toString().logE(this)
                })

    }

    private fun initTitle() {
        tv_default_top_title.text = "通讯录"
        iv_default_top_back.setOnClickListener { finish() }
    }

}