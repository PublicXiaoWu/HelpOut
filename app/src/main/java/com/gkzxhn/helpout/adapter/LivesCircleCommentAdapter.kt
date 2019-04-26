package com.gkzxhn.helpout.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.entity.LivesCircleDetails
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.StringUtils
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import kotlinx.android.synthetic.main.item_lives_circle_comment.view.*
import kotlinx.android.synthetic.main.layout_comment_top.view.*

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/16 5:59 PM
 * @description：
 */

class LivesCircleCommentAdapter(private val mContext: Context) : RecyclerView.Adapter<LivesCircleCommentAdapter.ViewHolder>() {

     var mDatas: LivesCircleDetails? = null
    private var onItemClickListener: MultiItemTypeAdapter.OnItemClickListener? = null
    private var mCurrentIndex = -1

    /**
     *  获取当前项实体
     */
    fun getCurrentItem(): LivesCircleDetails.CircleoffriendsCommentsBean {
        return mDatas?.circleoffriendsComments!![mCurrentIndex]
    }

    fun setOnItemClickListener(onItemClickListener: MultiItemTypeAdapter.OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    /**
     * 更新数据
     */
    fun updateItems(mDatas: LivesCircleDetails) {
        this.mDatas=mDatas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 11) {
            val view = LayoutInflater.from(mContext).inflate(R.layout.layout_comment_top, null)
            view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            return ViewHolder(view)
        } else {
            val view = LayoutInflater.from(mContext).inflate(R.layout.item_lives_circle_comment, null)
            view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            return ViewHolder(view)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            11
        }else{
            super.getItemViewType(position)
        }
    }

    class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!)

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /****** 头布局 ******/
        if (holder.itemViewType == 11) {
            val circleoffriendsPicture = mDatas?.circleoffriendsPicture!!
            val pictureList = ArrayList<String>()
            for (picture in circleoffriendsPicture) {
                pictureList.add(picture.fileId!!)
            }
            with(holder.itemView) {
                tv_lives_circle_content.text = mDatas?.content
                tv_lives_circle_like_number_show.text = "点赞 " + mDatas?.praiseNum
                tv_lives_circle_comment_number_show.text = "评论 " + mDatas?.commentNum
                tv_lives_circle_time.text = StringUtils.parseDate(mDatas?.createdTime)
                ll_lives_circle_image.setUrlList(pictureList)
                ProjectUtils.loadRoundImageByUserName(mContext, mDatas?.username, iv_lives_circle_avatar)
                tv_lives_circle_name.text=mDatas?.customer?.name

            }
        }else{
            val itemData = mDatas?.circleoffriendsComments!![position - 1]
            with(holder.itemView) {
                tv_item_lives_circle_comment_name.text= itemData.customerName
                tv_item_lives_circle_comment_time.text= StringUtils.parseDate(itemData.createdTime)
                tv_item_lives_circle_comment_content.text= itemData.content
                ProjectUtils.loadRoundImageByUserName(mContext, itemData.username, iv_item_lives_circle_comment_avatar)
            }
        }
    }

    override fun getItemCount(): Int {
        if (mDatas == null) {
            return 0
        } else {
            return mDatas?.circleoffriendsComments?.size!!+1
        }
    }


}