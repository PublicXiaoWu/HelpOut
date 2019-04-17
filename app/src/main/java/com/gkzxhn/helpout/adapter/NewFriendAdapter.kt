package com.gkzxhn.helpout.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gkzxhn.helpout.R
import com.netease.nimlib.sdk.msg.model.SystemMessage

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/16 5:59 PM
 * @description：
 */
class NewFriendAdapter(datas: List<SystemMessage>?) : BaseQuickAdapter<SystemMessage, BaseViewHolder>(R.layout.item_new_friend, datas) {
    override fun convert(helper: BaseViewHolder?, item: SystemMessage?) {
        helper?.setText(R.id.tv_name, item?.fromAccount)
                ?.setText(R.id.tv_msg, item?.content)
    }
}