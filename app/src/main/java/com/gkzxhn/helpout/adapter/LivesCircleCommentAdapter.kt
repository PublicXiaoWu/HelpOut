package com.gkzxhn.helpout.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.entity.LivesCircleDetails
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.StringUtils

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/16 5:59 PM
 * @description：
 */
class LivesCircleCommentAdapter(datas: List<LivesCircleDetails.CircleoffriendsCommentsBean>?) : BaseQuickAdapter<LivesCircleDetails.CircleoffriendsCommentsBean, BaseViewHolder>(R.layout.item_lives_circle_comment, datas) {

    override fun convert(helper: BaseViewHolder?, item: LivesCircleDetails.CircleoffriendsCommentsBean) {

        helper?.setText(R.id.tv_item_lives_circle_comment_name, item.customerName)
                ?.setText(R.id.tv_item_lives_circle_comment_time, StringUtils.parseDate(item.createdTime!!))
                ?.setText(R.id.tv_item_lives_circle_comment_content, item.content)
                ?.addOnClickListener(R.id.iv_item_lives_circle_comment_avatar)

        ProjectUtils.loadRoundImageByUserName(mContext, item.username, helper?.getView(R.id.iv_item_lives_circle_comment_avatar)!!)

    }


    /****** 更改某条数据之后局部刷新 ******/
    fun setDataChange(position: Int, contentBean: LivesCircleDetails.CircleoffriendsCommentsBean) {
        data[position] = contentBean
        notifyItemChanged(position, 666)
    }

    override fun getItemId(position: Int): Long {
        return if (data.isNotEmpty()) {
            if (position == 0) {
                 1
            } else {
                data[position-1].id!!.hashCode().toLong()
            }
        } else {
            1
        }

    }

}