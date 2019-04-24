package com.gkzxhn.helpout.adapter

import android.util.Log
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.entity.RxBusBean
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.view.SwipeMenuLayout
import com.netease.nim.uikit.common.ui.imageview.HeadImageView
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.msg.SystemMessageService
import com.netease.nimlib.sdk.msg.constant.SystemMessageStatus
import com.netease.nimlib.sdk.msg.model.SystemMessage
import org.json.JSONObject

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/16 5:59 PM
 * @description：
 */
class NewFriendAdapter(datas: List<SystemMessage>?) : BaseQuickAdapter<SystemMessage, BaseViewHolder>(R.layout.item_new_friend, datas) {

    override fun convert(helper: BaseViewHolder?, item: SystemMessage?) {
        Log.e("xiaowu_NewFriendAdapter", item?.content)
        if (item?.content!!.isEmpty()) {
            helper?.itemView?.visibility=View.GONE
            return
        }
        val verifyContent = JSONObject(item.content).getString("verifyContent")
        val userName = JSONObject(item.content).getString("nickName")

        helper?.setText(R.id.tv_name, userName)
                ?.setText(R.id.tv_msg, verifyContent)
                ?.setOnClickListener(R.id.tv_new_friend_item_add, add(helper, item))
                ?.setOnClickListener(R.id.tv_new_friend_item_delete, delete(helper, item))

        if (item.status == SystemMessageStatus.init) {
            helper?.setVisible(R.id.tv_new_friend_item_add, true)
            helper?.setVisible(R.id.tv_new_friend_item_state, false)
        } else {
            helper?.setVisible(R.id.tv_new_friend_item_add, false)
            helper?.setVisible(R.id.tv_new_friend_item_state, true)
            val verifyNotificationDealResult = getVerifyNotificationDealResult(item)
            helper?.setText(R.id.tv_new_friend_item_state, verifyNotificationDealResult)
        }
        /****** 通过网易ID加载头像 ******/
        helper?.getView<HeadImageView>(R.id.iv_item_new_friend_avatar)?.loadBuddyAvatar(item.fromAccount)
    }

    fun getVerifyNotificationDealResult(message: SystemMessage): String {
        return if (message.status == SystemMessageStatus.passed) {
            "已同意"
        } else if (message.status == SystemMessageStatus.declined) {
            "已拒绝"
        } else if (message.status == SystemMessageStatus.ignored) {
            "已忽略"
        } else if (message.status == SystemMessageStatus.expired) {
            "已过期"
        } else {
            "未处理"
        }
    }

    fun add(helper: BaseViewHolder?, message: SystemMessage?): View.OnClickListener? {
        return View.OnClickListener {
            NIMClient.getService(FriendService::class.java).ackAddFriendRequest(message?.fromAccount, true).setCallback(object : RequestCallback<Void> {
                override fun onSuccess(p0: Void?) {
                    onProcessSuccess(true, message!!)
                    helper?.setVisible(R.id.tv_new_friend_item_add, false)
                    helper?.setVisible(R.id.tv_new_friend_item_state, true)
                    helper?.setText(R.id.tv_new_friend_item_state, "已同意")
                    val curUserName = JSONObject(message.content).getString("curUserName")

                    RxBus.instance.post(RxBusBean.AddFriendPass(curUserName, message.fromAccount))
                }

                override fun onFailed(p0: Int) {
                    mContext.showToast("添加失败")
                    onProcessFailed(p0, message!!)

                }

                override fun onException(p0: Throwable?) {
                    mContext.showToast("添加异常")

                }
            })
        }
    }

    private fun delete(helper: BaseViewHolder?, message: SystemMessage?): View.OnClickListener? {
        return View.OnClickListener {
            (helper?.itemView as SwipeMenuLayout).quickClose()
            onDelete(message!!)
            data.remove(message)
            notifyDataSetChanged()
        }
    }


    private fun onProcessSuccess(pass: Boolean, message: SystemMessage) {
        val status = if (pass) SystemMessageStatus.passed else SystemMessageStatus.declined
        NIMClient.getService(SystemMessageService::class.java).setSystemMessageStatus(message.messageId, status)
        message.status = status
    }

    private fun onDelete(message: SystemMessage) {
        NIMClient.getService(SystemMessageService::class.java).deleteSystemMessage(message.messageId)

    }

    private fun onProcessFailed(code: Int, message: SystemMessage) {
        if (code == 408) {
            return
        }
        val status = SystemMessageStatus.expired
        NIMClient.getService(SystemMessageService::class.java).setSystemMessageStatus(message.messageId,
                status)
        message.status = status
    }

}