package com.gkzxhn.helpout.activity

import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.adapter.NewFriendAdapter
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.customview.RecyclerSpace
import com.gkzxhn.helpout.entity.RxBusBean
import com.gkzxhn.helpout.extensions.dp2px
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.RetrofitClientChat
import com.gkzxhn.helpout.utils.logE
import com.google.gson.Gson
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.msg.SystemMessageService
import com.netease.nimlib.sdk.msg.constant.SystemMessageType
import com.netease.nimlib.sdk.msg.model.SystemMessage
import kotlinx.android.synthetic.main.activity_new_friend.*
import kotlinx.android.synthetic.main.default_top.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.HashSet
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.collections.set


/**
 * @classname：AddFriendActivity
 * @author：liushaoxiang
 * @date：2019/4/15 2:43 PM
 * @description：新朋友
 */
class NewFriendActivity : BaseActivity() {
    private val itemIds = HashSet<Long>()
    private val addFriendVerifyRequestAccounts = HashSet<String>() // 发送过好友申请的账号（好友申请合并用）
    private val items = ArrayList<SystemMessage>()
    private var mAdapter: NewFriendAdapter? = null
    override fun provideContentViewId(): Int {
        return R.layout.activity_new_friend
    }

    override fun init() {
        initTopTitle()

        rcv_new_friend_top.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = NewFriendAdapter(items)
        mAdapter?.openLoadAnimation()
        rcv_new_friend_top.addItemDecoration(RecyclerSpace(0.5f.dp2px().toInt(), ContextCompat.getColor(this, R.color.gray_line)))
        rcv_new_friend_top.adapter = mAdapter
        loadData()


        /****** 收到添加好友验证通过的消息 ******/
        RxBus.instance.toObserverable(RxBusBean.AddFriendPass::class.java)
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    addFriend(it.userName, it.account)
                }, {
                    it.message.toString().logE(this)
                })

    }

    private fun loadData() {

        val types = ArrayList<SystemMessageType>()
        types.add(SystemMessageType.AddFriend)
        val addFriendList = NIMClient.getService(SystemMessageService::class.java).querySystemMessageByType(types, 0, 100)
        addFriendList.setCallback(object : RequestCallback<MutableList<SystemMessage>> {
            override fun onSuccess(addFriendList: MutableList<SystemMessage>?) {
                for (m in addFriendList!!) {
                    // 去重
                    if (duplicateFilter(m)) {
                        continue
                    }
                    // 同一个账号的好友申请仅保留最近一条
                    if (addFriendVerifyFilter(m)) {
                        continue
                    }
                    // 保存有效消息
                    items.add(m)
                }


                if (items.isEmpty()) {
                    mAdapter?.setEmptyView(R.layout.empty_view, rcv_new_friend_top)
                }
            }

            override fun onFailed(p0: Int) {
            }

            override fun onException(p0: Throwable?) {
            }

        })

    }


    // 去重
    private fun duplicateFilter(msg: SystemMessage): Boolean {
        if (itemIds.contains(msg.messageId)) {
            return true
        }

        itemIds.add(msg.messageId)
        return false
    }

    // 同一个账号的好友申请仅保留最近一条
    private fun addFriendVerifyFilter(msg: SystemMessage): Boolean {
        if (addFriendVerifyRequestAccounts.contains(msg.fromAccount)) {
            return true // 过滤
        }

        addFriendVerifyRequestAccounts.add(msg.fromAccount)
        return false // 不过滤
    }


    private fun initTopTitle() {
        tv_default_top_title.text = "新的朋友"
        iv_default_top_back.setOnClickListener {
            finish()
        }

    }


    /**
     * @methodName： created by liushaoxiang on 2019/4/23 10:56 AM.
     * @description：添加好友成功后通知后台绑定好友关系
     */
    fun addFriend(userName: String, account: String) {
        val map = LinkedHashMap<String, String>()
        map["username"] = userName
        map["account"] = account
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Gson().toJson(map))
        mCompositeSubscription.add(RetrofitClientChat
                .getInstance(this).mApi.addFriend(body)
                .subscribeOn(Schedulers.io())
                ?.unsubscribeOn(AndroidSchedulers.mainThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<ResponseBody>(this) {
                    override fun success(t: ResponseBody) {
                        Log.e("xiaowu","添加好友成功")
                    }

                }))
    }

    override fun onStart() {
        RxBus.instance.post(RxBusBean.AddPoint(false))
        super.onStart()

    }

    override fun onResume() {
        super.onResume()
        loadData()
    }


}