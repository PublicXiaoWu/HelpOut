package com.gkzxhn.helpout.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.entity.UIInfo.LawChannel

/**
 * Created by 方 on 2018/3/29.
 */
class LawsServiceAdapter(datas: List<LawChannel>?) :
        BaseQuickAdapter<LawChannel, BaseViewHolder>(R.layout.item_laws_service, datas) {
    override fun convert(helper: BaseViewHolder?, item: LawChannel?) {
        helper?.setImageResource(R.id.iv_channel, item?.imgRes ?: R.mipmap.icon_labour_employment)
                ?.setText(R.id.tv_title, item?.name)
                ?.setText(R.id.tv_desc, item?.desc)
    }
}