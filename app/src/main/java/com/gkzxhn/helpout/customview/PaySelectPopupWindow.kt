package com.gkzxhn.helpout.customview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.extensions.dp2px


/**
 * Created by 方 on 2018/4/17.
 */
class PaySelectPopupWindow : PopupWindow {

    private lateinit var mMenuView: View
    private lateinit var activity: Activity

    constructor(activity: Activity, onItemClickListener: onItemClickListener) : super(activity as Context) {
        val inflater = activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mMenuView = inflater.inflate(R.layout.pay_popup_window, null, false)
        this.activity = activity

        //设置SelectPicPopupWindow的View
        this.contentView = mMenuView
        //设置SelectPicPopupWindow弹出窗体的宽
        this.width = 330f.dp2px().toInt()
        //设置SelectPicPopupWindow弹出窗体的高
        this.height = 315f.dp2px().toInt()
        //设置SelectPicPopupWindow弹出窗体可点击
        this.isFocusable = true
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.animationStyle = R.style.AnimBottom
        //实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(0x999999)
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw)

        mMenuView.findViewById<FrameLayout>(R.id.fl_close).setOnClickListener {
            onItemClickListener.onClick(this@PaySelectPopupWindow, it)
        }

        mMenuView.findViewById<LinearLayout>(R.id.ll_alipay).setOnClickListener {
            mMenuView.findViewById<CheckBox>(R.id.cb_ali_pay).isChecked = true
            mMenuView.findViewById<CheckBox>(R.id.cb_wx_pay).isChecked = false
            onItemClickListener.onClick(this@PaySelectPopupWindow, it)
        }

        mMenuView.findViewById<LinearLayout>(R.id.ll_wx_pay).setOnClickListener {
            mMenuView.findViewById<CheckBox>(R.id.cb_ali_pay).isChecked = false
            mMenuView.findViewById<CheckBox>(R.id.cb_wx_pay).isChecked = true
            onItemClickListener.onClick(this@PaySelectPopupWindow, it)
        }

        mMenuView.findViewById<Button>(R.id.bt_pay_confirm).setOnClickListener {
            onItemClickListener.onClick(this@PaySelectPopupWindow, it)
        }

        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener { v, event ->
            val height = (mMenuView.findViewById(R.id.ll_pop_pay) as LinearLayout).getTop()
            val y = event.y.toInt()
            if (event.action == MotionEvent.ACTION_UP) {
                if (y < height) {
                    dismiss()
                }
            }
            true
        }
    }

    override fun dismiss() {
        super.dismiss()
        setBackgroundAlpha(1.0f)
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = activity.window
                .attributes
        lp.alpha = bgAlpha
        activity.window.attributes = lp
    }

    @SuppressLint("SetTextI18n")
    fun setAmount(amount: Double) {
        mMenuView.findViewById<TextView>(R.id.tv_amount).text = "¥${amount.toString()}"
    }

    interface onItemClickListener {
        fun onClick(popupWindow: PopupWindow, view: View)
    }
}