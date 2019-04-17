package com.gkzxhn.helpout.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.entity.LivesCircle

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/16 5:59 PM
 * @description：
 */
class LivesCircleAdapter(datas: List<LivesCircle>?) : BaseQuickAdapter<LivesCircle, BaseViewHolder>(R.layout.item_lives_circle, datas) {

    override fun convert(helper: BaseViewHolder?, item: LivesCircle?) {
        helper?.setText(R.id.tv_item_lives_circle_name, item?.name)
                ?.setText(R.id.tv_item_lives_circle_time, item?.time)
                ?.setText(R.id.tv_item_lives_circle_content, item?.context)
                ?.setText(R.id.tv_item_lives_circle_image_number, item?.imageNumber.toString())
                ?.setText(R.id.tv_item_lives_circle_like_number, item?.like.toString())
    }
}