package com.gkzxhn.helpout.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.entity.OrderReceivingContent
import com.gkzxhn.helpout.entity.UIInfo.LawChannel
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.StringUtils
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import kotlinx.android.synthetic.main.item_order_receiving.view.*
import java.util.*

/**
 * Explanation：
 * @author LSX
 * Created on 2018/9/10.
 */

class OrderReceivingAdapter(private val mContext: Context) : RecyclerView.Adapter<OrderReceivingAdapter.ViewHolder>() {

    private var mDatas: ArrayList<OrderReceivingContent> = ArrayList()
    private var onItemClickListener: MultiItemTypeAdapter.OnItemClickListener? = null
    private var mCurrentIndex = -1

    /**
     *  获取当前项实体
     */
    fun getCurrentItem(): OrderReceivingContent {
        return mDatas[mCurrentIndex]
    }

    fun setOnItemClickListener(onItemClickListener: MultiItemTypeAdapter.OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }


    /****** 回调抢单事件 ******/
    private var onItemRushListener: ItemRushListener? = null

    fun setOnItemRushListener(ItemRushListener: ItemRushListener) {
        this.onItemRushListener = ItemRushListener
    }

    interface ItemRushListener {
        fun onRushListener()
    }


    /**
     * 更新数据
     */
    fun updateItems(clear: Boolean, mDatas: List<OrderReceivingContent>?) {
        if (clear) {
            this.mDatas.clear()
        }
        if (mDatas != null && mDatas.isNotEmpty()) {
            this.mDatas.addAll(mDatas)
        }else{
            this.mDatas.clear()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_order_receiving, null)
        view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        return ViewHolder(view)
    }

    class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!)


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView) {
            val entity = mDatas[position]
            /****** 姓名 ******/
            tv_item_order_receiving_name.text = entity.customer!!.name
            ProjectUtils.loadRoundImageByUserName(context,entity.customer!!.username,iv_item_order_receiving)
            /****** 赏金  ******/
            tv_main_top_end.text = "￥" + entity.reward.toString()
            tv_item_order_receiving_time.text = StringUtils.parseDate(entity.createdTime!!)

            v_item_order_receiving_type.text= LawChannel.find(entity.category!!)?.name
            /****** 正在进行中的订单 ******/
            val processingOrderId = App.SP.getString(Constants.PROCESSING_ORDER_ID, "")
            /****** 有正在进行的单时 按扭置灰  认证未通过时也置灰 ******/
            if (!ProjectUtils.certificationStatus()|| processingOrderId.isNotEmpty()) {
                /****** 认证未通过 按扭变成灰色 ******/
                tv_item_order_receiving_rush.setBackgroundResource(R.drawable.shape_order_bg_select_gary)
            }else{
                tv_item_order_receiving_rush.setBackgroundResource(R.drawable.selector_shape_order_bg)
            }

            holder.itemView.setOnClickListener {
                mCurrentIndex = position
                onItemClickListener?.onItemClick(this, holder, position)
            }

            tv_item_order_receiving_rush.setOnClickListener {
                mCurrentIndex = position
                onItemRushListener?.onRushListener()
            }

        }
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

}
