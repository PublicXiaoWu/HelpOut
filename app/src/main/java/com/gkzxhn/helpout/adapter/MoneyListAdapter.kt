package com.gkzxhn.helpout.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.entity.MoneyList
import com.gkzxhn.helpout.utils.StringUtils
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import kotlinx.android.synthetic.main.item_money_list.view.*
import java.util.*

/**
 * Explanation：账单
 * @author LSX
 * Created on 2018/9/10.
 */

class MoneyListAdapter(private val mContext: Context) : RecyclerView.Adapter<MoneyListAdapter.ViewHolder>() {

    private var mDatas: ArrayList<MoneyList.ContentBean> = ArrayList()
    private var onItemClickListener: MultiItemTypeAdapter.OnItemClickListener? = null
    private var mCurrentIndex = -1

    /**
     *  获取当前项实体
     */
    fun getCurrentItem(): MoneyList.ContentBean {
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
    fun updateItems(clear: Boolean, mDatas: List<MoneyList.ContentBean>?) {
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
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_money_list, null)
        view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        return ViewHolder(view)
    }

    class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!)

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView) {
            val entity = mDatas[position]
            val type = entity.type.toString()
            val source = entity.source.toString()
            tv_money_list_money.text = entity.amount.toString()
            val payType = when (source) {
                "ALIPAY" -> mContext.getString(R.string.alipay)
                "WE_CHAT_PAY" -> mContext.getString(R.string.weixin)
                else -> "Other"
            }
            val typeStr = when (type) {
                "SERVICE_CHARGE" -> {
                    tv_money_list_money.setTextColor(resources.getColor(R.color.main_bottom_black))
                    "提现平台使用费"
                }
                "PAYMENT" -> {
                    tv_money_list_money.setTextColor(resources.getColor(R.color.main_bottom_black))
                    "法律咨询费用"
                }
                "REFUND" -> {
                    tv_money_list_money.setTextColor(resources.getColor(R.color.main_orange))
                    "法律咨询退费"
                }
                "WITHDRAW" -> {
                    tv_money_list_money.setTextColor(resources.getColor(R.color.main_bottom_black))
                    "账户提现"
                }
                else -> "Other"
            }
            tv_money_list_type.text = typeStr
            tv_money_list_number.text = "订单编号：${entity.orderNumber}"
            tv_money_list_time.text = StringUtils.parseDate(entity.successTime.toString())

        }
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

}
