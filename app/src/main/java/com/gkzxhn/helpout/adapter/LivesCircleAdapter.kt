package com.gkzxhn.helpout.adapter

import android.text.TextUtils
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.entity.LivesCircle
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.StringUtils
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.view.NineGridTestLayout

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/16 5:59 PM
 * @description：
 */
class LivesCircleAdapter(datas: List<LivesCircle.ContentBean>?) : BaseQuickAdapter<LivesCircle.ContentBean, BaseViewHolder>(R.layout.item_lives_circle, datas) {

    override fun convert(helper: BaseViewHolder?, item: LivesCircle.ContentBean) {
        /****** 整理图片list ******/
        val circleoffriendsPicture = item.circleoffriendsPicture
        val localImages = item.localImages
        var pictureList = ArrayList<Any>()
        if (circleoffriendsPicture != null) {
            for (picture in circleoffriendsPicture) {
                pictureList.add(picture.fileId!!)
            }
        } else if (localImages != null) {
            pictureList = ArrayList(localImages)
        }

        val myName = App.SP.getString(Constants.SP_NAME, "")
        helper?.setText(R.id.tv_item_lives_circle_name, if (TextUtils.isEmpty(item.customer?.name))
            myName else item.customer?.name)
                ?.setText(R.id.tv_item_lives_circle_time, StringUtils.parseDate(item.createdTime!!))
                ?.setText(R.id.tv_item_lives_circle_content, item.content)
                ?.setText(R.id.tv_item_lives_circle_comment_number, item.commentNum.toString())
                ?.setText(R.id.tv_item_lives_circle_like_number, item.praiseNum.toString())
                ?.setOnClickListener(R.id.iv_item_lives_circle_share, share())
                ?.getView<NineGridTestLayout>(R.id.item_lives_circle_image)?.setUrlList(pictureList)
        if (item.id != null) helper?.addOnClickListener(R.id.v_item_lives_circle_like_number)

        if (TextUtils.isEmpty(item.username)) {
            ProjectUtils.loadMyIcon(mContext, helper?.getView(R.id.iv_item_lives_circle_avatar)!!)
        } else {
            ProjectUtils.loadRoundImageByUserName(mContext, item.username, helper?.getView(R.id.iv_item_lives_circle_avatar)!!)
        }
        if (item.praisesCircleoffriends) {
            helper?.setImageResource(R.id.iv_item_lives_circle_like_number, R.mipmap.ic_lives_liked)
        } else {
            helper?.setImageResource(R.id.iv_item_lives_circle_like_number, R.mipmap.ic_lives_like)
        }

    }

    /****** 分享 ******/
    private fun share(): View.OnClickListener? {
        return View.OnClickListener() {
            mContext.showToast("敬请期待")
        }
    }

    /****** 更改某条数据之后局部刷新 ******/
    fun setDataChange(position: Int, contentBean: LivesCircle.ContentBean) {
        data[position] = contentBean
        notifyItemChanged(position, 666)
    }


    override fun getItemId(position: Int): Long {
        if (data.isNotEmpty()) {
            return data[position].id?.hashCode()?.toLong() ?: 1
        } else {
            return 1
        }

    }


}