package com.gkzxhn.helpout.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.entity.UIInfo.NormalListItem

/**
 * Created by 方 on 2018/3/19.
 */

class FeedbackTypesAdapter(datas: List<NormalListItem>?) : BaseQuickAdapter<NormalListItem, BaseViewHolder>(R.layout.item_feedback_types, datas) {

    var checkedPosition = -1

    override fun convert(helper: BaseViewHolder?, item: NormalListItem?) {
        val isChecked = item?.isCheck ?: false
        if (isChecked) checkedPosition = helper?.adapterPosition?.let { it - headerLayoutCount } ?: -1
        helper?.setText(R.id.tv_title, item?.text)
                ?.setChecked(R.id.cb_feedback_types, isChecked)
    }
}