package com.gkzxhn.helpout.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.entity.OrderDispose
import com.gkzxhn.helpout.entity.UIInfo.LawChannel
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.StringUtils
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import kotlinx.android.synthetic.main.item_order_dispose.view.*
import java.util.*

/**
 * Explanation： 我的 （指定单）
 * @author LSX
 * Created on 2018/9/10.
 */

class OrderDisposeAdapter(private val mContext: Context, private val data: List<OrderDispose.ContentBean>?) : RecyclerView.Adapter<OrderDisposeAdapter.ViewHolder>() {

    private var mDatas: ArrayList<OrderDispose.ContentBean> = ArrayList()
    private var onItemClickListener: MultiItemTypeAdapter.OnItemClickListener? = null
    private var mCurrentIndex = -1

    /**
     *  获取当前项实体
     */
    fun getCurrentItem(): OrderDispose.ContentBean {
        return mDatas[mCurrentIndex]
    }

    fun setOnItemClickListener(onItemClickListener: MultiItemTypeAdapter.OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    /****** 回调拒单 接单事件 ******/
    private var onItemOrderListener: ItemOrderListener? = null

    fun setOnItemOrderListener(ItemOrderListener: ItemOrderListener) {
        this.onItemOrderListener = ItemOrderListener
    }

    interface ItemOrderListener {
        fun onRefusedListener()
    }

    /**
     * 更新数据
     */
    fun updateItems(clear: Boolean, mDatas: List<OrderDispose.ContentBean>?) {
        if (clear) {
            this.mDatas.clear()
        }
        if (mDatas != null && mDatas.isNotEmpty()) {
            this.mDatas.addAll(mDatas)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_order_dispose, null)
        view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        return ViewHolder(view)
    }

    class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!)

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!ProjectUtils.certificationStatus()) {
            //填充用户数据
            fillCustomerData(holder, position)
            return
        }
        with(holder.itemView) {
            val entity = mDatas[position]
            tv_order_dispose_name.text = entity.customer!!.name
            ProjectUtils.loadRoundImageByUserName(context, entity.customer!!.username, iv_order_dispose_item)
            v_item_order_receiving_type.text = LawChannel.find(entity.category!!)?.name
            /****** 价格 ******/
            tv_main_top_end.text = "￥" + entity.reward
            tv_order_dispose_time.text = StringUtils.parseDate(entity.createdTime)
            view_notify_red_point.visibility = View.VISIBLE.takeIf { entity.missedCallStatus == 0 }?:View.GONE

            when (entity.status) {
                Constants.ORDER_STATE_ACCEPTED -> {
                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_yjd)
                    tv_order_dispose_open.visibility = View.GONE
                    tv_tv_order_next.visibility = View.VISIBLE
                }
                Constants.ORDER_STATE_PROCESSING -> {
                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_clz)
                    tv_order_dispose_open.visibility = View.VISIBLE
                    tv_tv_order_next.visibility = View.GONE
                }
                Constants.ORDER_STATE_COMPLETE -> {
                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_ywc)
                    tv_order_dispose_open.visibility = View.VISIBLE
                    tv_tv_order_next.visibility = View.GONE
                }
                Constants.ORDER_STATE_CANCELLED -> {
                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_yqx)
                    tv_order_dispose_open.visibility = View.VISIBLE
                    tv_tv_order_next.visibility = View.GONE
                }
                Constants.ORDER_STATE_REFUSED -> {
                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_ygb)
                    tv_order_dispose_open.visibility = View.VISIBLE
                    tv_tv_order_next.visibility = View.GONE
                }
                else -> {
                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_clz)
                    tv_order_dispose_open.visibility = View.VISIBLE
                    tv_tv_order_next.visibility = View.GONE

                }
            }

            holder.itemView.setOnClickListener {
                mCurrentIndex = position
                onItemClickListener?.onItemClick(this, holder, position)
            }

            tv_tv_order_next.setOnClickListener {
                mCurrentIndex = position
                onItemOrderListener?.onRefusedListener()
            }
        }
    }

    fun fillCustomerData(holder: ViewHolder, position: Int) {
        with(holder.itemView) {
            val entity = mDatas[position]
            tv_order_dispose_name.text = "￥${entity.reward}"
            tv_order_dispose_name.setTextColor(context.resources.getColor(R.color.orange))

            if (TextUtils.isEmpty(entity.lawyer?.username)) {
                entity.category?.let { LawChannel.find(it)?.imgRes }?.let { iv_order_dispose_item.setImageResource(it)}
            }else {
                ProjectUtils.loadRoundImageByUserName(context, entity.lawyer?.username, iv_order_dispose_item)
            }
            v_item_order_receiving_type.text = entity.category?.let { LawChannel.find(it)?.name }?:""
            /****** 价格 ******/
            tv_main_top_end.visibility = View.GONE
            tv_order_dispose_time.text = StringUtils.parseDate(entity.createdTime)

            view_notify_red_point.visibility = View.VISIBLE.takeIf { entity.missedCallStatus == 1 }?:View.GONE
            when (entity.status) {
                Constants.ORDER_STATE_PENDING_PAYMENT -> {
                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_dzf)
                    tv_order_dispose_open.visibility = View.VISIBLE
                    tv_tv_order_next.visibility = View.GONE
                }
                Constants.ORDER_STATE_PENDING_RECEIVING -> {
                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_djd)
                    tv_order_dispose_open.visibility = View.VISIBLE
                    tv_tv_order_next.visibility = View.GONE
                }
                Constants.ORDER_STATE_ACCEPTED -> {
                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_yjd)
                    tv_order_dispose_open.visibility = View.GONE
                    tv_tv_order_next.visibility = View.VISIBLE
                }
                Constants.ORDER_STATE_PROCESSING -> {
                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_clz)
                    tv_order_dispose_open.visibility = View.VISIBLE
                    tv_tv_order_next.visibility = View.GONE
                }
                Constants.ORDER_STATE_COMPLETE -> {
                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_ywc)
                    tv_order_dispose_open.visibility = View.VISIBLE
                    tv_tv_order_next.visibility = View.GONE
                }
                Constants.ORDER_STATE_CANCELLED -> {
                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_yqx)
                    tv_order_dispose_open.visibility = View.VISIBLE
                    tv_tv_order_next.visibility = View.GONE
                }
                Constants.ORDER_STATE_REFUSED -> {
                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_ygb)
                    tv_order_dispose_open.visibility = View.VISIBLE
                    tv_tv_order_next.visibility = View.GONE
                }
                else -> {
                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_clz)
                    tv_order_dispose_open.visibility = View.VISIBLE
                    tv_tv_order_next.visibility = View.GONE

                }
            }

            holder.itemView.setOnClickListener {
                mCurrentIndex = position
                onItemClickListener?.onItemClick(this, holder, position)
            }

            tv_tv_order_next.setOnClickListener {
                mCurrentIndex = position
                onItemOrderListener?.onRefusedListener()
            }
        }
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

}
