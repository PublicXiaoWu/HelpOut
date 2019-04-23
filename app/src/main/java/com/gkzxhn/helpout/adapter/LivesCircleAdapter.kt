package com.gkzxhn.helpout.adapter

import android.content.Intent
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.LivesCircleDetailsActivity
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
        val circleoffriendsPicture = item.circleoffriendsPicture!!
        val pictureList=ArrayList<String>()
        for (picture in circleoffriendsPicture) {
            pictureList.add(picture.fileId!!)
        }

        helper?.setText(R.id.tv_item_lives_circle_name, item.customer?.name)
                ?.setText(R.id.tv_item_lives_circle_time, StringUtils.parseDate(item.createdTime!!))
                ?.setText(R.id.tv_item_lives_circle_content, item.content)
                ?.setText(R.id.tv_item_lives_circle_comment_number, item.commentNum.toString())
                ?.setText(R.id.tv_item_lives_circle_like_number, item.praiseNum.toString())
                ?.setOnClickListener(R.id.iv_item_lives_circle_share, share())
                ?.setOnClickListener(R.id.tv_item_lives_circle_share, share())
                ?.setOnClickListener(R.id.tv_item_lives_circle_comment, comment())
                ?.setOnClickListener(R.id.tv_item_lives_circle_comment_number, comment())
                ?.setOnClickListener(R.id.iv_item_lives_circle_like_number, like(helper, item))
                ?.setOnClickListener(R.id.tv_item_lives_circle_like_number, like(helper, item))
                ?.getView<NineGridTestLayout>(R.id.item_lives_circle_image)?.setUrlList(pictureList)

        ProjectUtils.loadMyIcon(mContext,helper?.getView(R.id.iv_item_lives_circle_avatar)!!)
    }

    /****** 分享 ******/
    private fun share(): View.OnClickListener? {
        return View.OnClickListener() {
            mContext.showToast("敬请期待")
        }


    }

    /****** 评论 ******/
    private fun comment(): View.OnClickListener? {
        return View.OnClickListener() {
            mContext.startActivity(Intent(mContext, LivesCircleDetailsActivity::class.java))
        }
    }

    /****** 点赞 ******/
    private fun like(helper: BaseViewHolder, item: LivesCircle.ContentBean?): View.OnClickListener? {
        val i = item?.praiseNum!! + 1
        return View.OnClickListener() {
            helper.setText(R.id.tv_item_lives_circle_like_number, i.toString())
            helper.setImageResource(R.id.iv_item_lives_circle_like_number, R.mipmap.ic_lives_liked)
        }


    }

}