package com.gkzxhn.helpout.adapter

import android.text.TextUtils
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.entity.Feedback
import com.gkzxhn.helpout.utils.DateUtil

/**
 * Created by 方 on 2018/3/29.
 */
class FeedBackAdapter(datas: List<Feedback>?) :
        BaseQuickAdapter<Feedback, BaseViewHolder>(R.layout.item_feedback, datas) {
    override fun convert(helper: BaseViewHolder?, item: Feedback?) {

        val time = try {
            item?.createdAt?.toLong()?.let {
                DateUtil.formatDateFromMillis(it, DateUtil.PATTERN_yyyyMMdd)
            } ?: ""
        } catch (e: Exception) {
            item?.createdAt ?: ""
        }
        val title = "${item?.typeName
                ?: ""}${if (TextUtils.isEmpty(item?.desc)) "" else ":${item?.desc}"}"
        helper!!.setText(R.id.tv_title_type, title)
                .setText(R.id.tv_content, item?.content ?: item?.contents ?: "")
                .setText(R.id.tv_time, time)
                .addOnClickListener(R.id.iv_evidence_pic1)
                .addOnClickListener(R.id.iv_evidence_pic2)
                .addOnClickListener(R.id.iv_evidence_pic3)
                .addOnClickListener(R.id.iv_evidence_pic4)
        val images = item?.imageUrls
                ?.let {
                    if (it.endsWith(";")) it.substring(0, it.length - 1) else it
                }?.split(";")
        val imageview1 = helper.getView<ImageView>(R.id.iv_evidence_pic1)
        val imageview2 = helper.getView<ImageView>(R.id.iv_evidence_pic2)
        val imageview3 = helper.getView<ImageView>(R.id.iv_evidence_pic3)
        val imageview4 = helper.getView<ImageView>(R.id.iv_evidence_pic4)
/*
        if (images?.isNotEmpty() == true && !TextUtils.isEmpty(images[0])) {
            helper.getView<LinearLayout>(R.id.ll_images).visibility = View.VISIBLE
            imageview1.loadRoundConner(imageview1.context, "${images[0]}?token=${Constants.IMAGE_TOKEN}", R.mipmap.img_error)
            imageview1.visibility = View.VISIBLE
            if (images.size > 1 && !TextUtils.isEmpty(images[1])) {
                imageview2.loadRoundConner(imageview2.context, "${images[1]}?token=${Constants.IMAGE_TOKEN}", R.mipmap.img_error)
                imageview2.visibility = View.VISIBLE
            } else {
                imageview2.visibility = View.INVISIBLE
            }
            if (images.size > 2 && !TextUtils.isEmpty(images[2])) {
                imageview3.loadRoundConner(imageview3.context, "${images[2]}?token=${Constants.IMAGE_TOKEN}", R.mipmap.img_error)
                imageview3.visibility = View.VISIBLE
            } else {
                imageview3.visibility = View.INVISIBLE
            }
            if (images.size > 3 && !TextUtils.isEmpty(images[3])) {
                imageview4.loadRoundConner(imageview4.context, "${images[3]}?token=${Constants.IMAGE_TOKEN}", R.mipmap.img_error)
                imageview4.visibility = View.VISIBLE
            } else {
                imageview4.visibility = View.INVISIBLE
            }
        } else {
            helper.getView<LinearLayout>(R.id.ll_images).visibility = View.GONE
        }
*/
    }
}