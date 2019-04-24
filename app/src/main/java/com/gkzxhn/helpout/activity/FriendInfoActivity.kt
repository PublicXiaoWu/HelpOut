package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.entity.FriendInfo
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.RetrofitClientChat
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.StringUtils
import com.gkzxhn.helpout.utils.showToast
import com.netease.nim.uikit.api.NimUIKit
import kotlinx.android.synthetic.main.activity_friend_info.*
import kotlinx.android.synthetic.main.default_top.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/19 9:14 AM
 * @description   好友信息
 */
class FriendInfoActivity : BaseActivity() {
    var isFriend = true
    var friendAccountID = ""
    var userName = ""

    override fun provideContentViewId(): Int {
        return R.layout.activity_friend_info
    }

    @SuppressLint("SetTextI18n")
    override fun init() {
        initTopTitle()

        val account = intent.getStringExtra("account")
        getFriendInfoByAccount(account)

        /****** 生活圈 ******/
        v_friend_info_lives_circle.setOnClickListener {

            val intent = Intent(this, LivesCircleActivity::class.java)
            intent.putExtra("LivesCircleType", 3)
            intent.putExtra("userName", userName)
            startActivity(intent)
            finish()
        }

        /****** 发消息 ******/
        v_friend_info_send_bg.setOnClickListener {
            if (isFriend) {
                NimUIKit.startP2PSession(this, friendAccountID)
            } else {
                showToast("你们还不是好友")
            }
        }
    }

    private fun initTopTitle() {
        tv_default_top_title.text = "好友信息"
        iv_default_top_back.setOnClickListener {
            finish()
        }

    }

    fun getFriendInfoByAccount(account: String) {
        mCompositeSubscription.add(RetrofitClientChat
                .getInstance(this).mApi.getFriendInfo(account = account)
                .subscribeOn(Schedulers.io())
                ?.unsubscribeOn(AndroidSchedulers.mainThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<FriendInfo>(this) {
                    override fun success(t: FriendInfo) {
                        /****** 是否是好友,1代表是，0代表不是 ******/
                        isFriend = t.friendinfo == "1"
                        friendAccountID= t.account!!
                        userName= t.username!!
                        ProjectUtils.loadRoundImageByUserName(this@FriendInfoActivity, t.username, iv_friend_info)
                        tv_friend_info_name.text = t.name
                        tv_friend_info_phone.text = StringUtils.phoneChange(t.phoneNumber!!)

                        if (t.circleoffriendsPicture!=null&&t.circleoffriendsPicture!!.isNotEmpty()) {
                            iv_friend_info_lives_circle_1.visibility=View.VISIBLE
                            ProjectUtils.loadImageByFileID(this@FriendInfoActivity, t.circleoffriendsPicture!![0].fileId, iv_friend_info_lives_circle_1)
                            if (t.circleoffriendsPicture!!.size > 1) {
                                iv_friend_info_lives_circle_2.visibility=View.VISIBLE

                                ProjectUtils.loadImageByFileID(this@FriendInfoActivity, t.circleoffriendsPicture!![1].fileId, iv_friend_info_lives_circle_2)
                            }
                            if (t.circleoffriendsPicture!!.size > 2) {
                                iv_friend_info_lives_circle_3.visibility=View.VISIBLE

                                ProjectUtils.loadImageByFileID(this@FriendInfoActivity, t.circleoffriendsPicture!![2].fileId, iv_friend_info_lives_circle_3)
                            }
                            if (t.circleoffriendsPicture!!.size > 3) {
                                iv_friend_info_lives_circle_4.visibility=View.VISIBLE

                                ProjectUtils.loadImageByFileID(this@FriendInfoActivity, t.circleoffriendsPicture!![3].fileId, iv_friend_info_lives_circle_4)
                            }
                        }
                    }

                }))
    }

}

