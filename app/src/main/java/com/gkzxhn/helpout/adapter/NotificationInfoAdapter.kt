package com.gkzxhn.helpout.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.entity.NotificationInfoList
import com.gkzxhn.helpout.utils.StringUtils
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import kotlinx.android.synthetic.main.item_notification_list.view.*
import org.json.JSONObject
import java.util.*


/**
 * Explanation：
 * @author LSX
 * Created on 2018/9/10.
 */

class NotificationInfoAdapter(private val mContext: Context) : RecyclerView.Adapter<NotificationInfoAdapter.ViewHolder>() {

    private var mDatas: ArrayList<NotificationInfoList.ContentBean> = ArrayList()
    private var onItemClickListener: MultiItemTypeAdapter.OnItemClickListener? = null
    private var mCurrentIndex = -1

    /**
     *  获取当前项实体
     */
    fun getCurrentItem(): NotificationInfoList.ContentBean {
        return mDatas[mCurrentIndex]
    }

    fun setOnItemClickListener(onItemClickListener: MultiItemTypeAdapter.OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }


    /**
     * 更新数据
     */
    fun updateItems(clear: Boolean, mDatas: List<NotificationInfoList.ContentBean>?) {
        if (clear) {
            this.mDatas.clear()
        }
        if (mDatas != null && mDatas.isNotEmpty()) {
            this.mDatas.addAll(mDatas)
        } else {
            this.mDatas.clear()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_notification_list, null)
        view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        return ViewHolder(view)
    }

    class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!)


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView) {
            val entity = mDatas[position]
            tv_notification_type.text = "通知消息"
            tv_notification_time.text = StringUtils.parseDate2(entity.createdTime.toString())

            val json = entity.content
            Log.e("xiaowu", json)
            try {
                val type = JSONObject(json).getString("type")
                val ext = JSONObject(json).getString("ext")
                val content = JSONObject(json).getString("content")
                tv_notification_context.text = content
                if (type=="RUSH_PAGE_REFRESH") {
                    tv_notification_context.text = "有新的可接订单，快去接单吧！"
                }
            } catch (e: Exception) {

            }
        }
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

}
