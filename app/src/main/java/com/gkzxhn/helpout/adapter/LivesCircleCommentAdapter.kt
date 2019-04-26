package com.gkzxhn.helpout.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.entity.LivesCircleDetails
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.StringUtils

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/16 5:59 PM
 * @description：
 */
class LivesCircleCommentAdapter(datas: List<LivesCircleDetails.CircleoffriendsCommentsBean>?) : BaseQuickAdapter<LivesCircleDetails.CircleoffriendsCommentsBean, BaseViewHolder>(R.layout.item_lives_circle_comment, datas) {

    override fun convert(helper: BaseViewHolder?, item: LivesCircleDetails.CircleoffriendsCommentsBean) {

        helper?.setText(R.id.tv_item_lives_circle_comment_name, item.customerName)
                ?.setText(R.id.tv_item_lives_circle_comment_time, StringUtils.parseDate(item.createdTime!!))
                ?.setText(R.id.tv_item_lives_circle_comment_content, item.content)
                ?.addOnClickListener(R.id.iv_item_lives_circle_comment_avatar)

        ProjectUtils.loadRoundImageByUserName(mContext, item.username, helper?.getView(R.id.iv_item_lives_circle_comment_avatar)!!)

    }


    /****** 更改某条数据之后局部刷新 ******/
    fun setDataChange(position: Int, contentBean: LivesCircleDetails.CircleoffriendsCommentsBean) {
        data[position] = contentBean
        notifyItemChanged(position, 666)
    }

    override fun getItemId(position: Int): Long {
        return if (data.isNotEmpty()) {
            if (position == 0) {
                 1
            } else {
                data[position-1].id!!.hashCode().toLong()
            }
        } else {
            1
        }

    }

}

//class LivesCircleCommentAdapter(private val mContext: Context) : RecyclerView.Adapter<AllOrderAdapter.ViewHolder>() {
//
//    private var mDatas: LivesCircleDetails=null
//    private var onItemClickListener: MultiItemTypeAdapter.OnItemClickListener? = null
//    private var mCurrentIndex = -1
//
//    /**
//     *  获取当前项实体
//     */
//    fun getCurrentItem(): LivesCircleDetails.CircleoffriendsCommentsBean {
//
//        return mDatas.circleoffriendsComments!![mCurrentIndex]
//    }
//
//    fun setOnItemClickListener(onItemClickListener: MultiItemTypeAdapter.OnItemClickListener?) {
//        this.onItemClickListener = onItemClickListener
//    }
//
//    /**
//     * 更新数据
//     */
//    fun updateItems(mDatas: LivesCircleDetails) {
//        this.mDatas=mDatas
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(mContext).inflate(R.layout.item_order_all_dispose, null)
//        view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//        return ViewHolder(view)
//    }
//
//    class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!)
//
//    @SuppressLint("SetTextI18n")
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        with(holder.itemView) {
//            val entity = mDatas[position]
//            tv_order_dispose_name.text = entity.customer!!.name
//            ProjectUtils.loadRoundImageByUserName(context, entity.customer!!.username, iv_order_dispose_item)
//            /****** 价格 ******/
//            tv_main_top_end.text = "￥" + entity.reward
//            tv_order_dispose_time.text = StringUtils.parseDate(entity.createdTime)
//            v_item_order_receiving_type.text = ProjectUtils.categoriesConversion(entity.category!!)
//
//            when (entity.status) {
//                Constants.ORDER_STATE_ACCEPTED ->
//                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_yjd)
//                Constants.ORDER_STATE_PROCESSING ->
//                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_clz)
//                Constants.ORDER_STATE_COMPLETE ->
//                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_ywc)
//                Constants.ORDER_STATE_CANCELLED ->
//                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_yqx)
//                Constants.ORDER_STATE_REFUSED ->{
//                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_ygb)
//                }
//                else ->
//                    iv_order_dispose_state.setImageResource(R.mipmap.ic_order_clz)
//            }
//            holder.itemView.setOnClickListener {
//                mCurrentIndex = position
//                onItemClickListener?.onItemClick(this, holder, position)
//            }
//
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return mDatas.circleoffriendsComments?.size!!+1
//    }
//
//}