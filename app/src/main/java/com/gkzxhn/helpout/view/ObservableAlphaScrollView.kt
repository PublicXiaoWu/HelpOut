package com.gkzxhn.helpout.view

import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet

/**
 * @classname：ObservableAlphaScrollView
 * @author：liushaoxiang
 * @date：2019/4/17 4:11 PM
 * @description：划动处理类
 */
class ObservableAlphaScrollView : NestedScrollView {
    private var mOnAlphaScrollChangeListener: OnAlphaScrollChangeListener? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    fun setOnAlphaScrollChangeListener(onAlphaScrollChangeListener: OnAlphaScrollChangeListener) {
        this.mOnAlphaScrollChangeListener = onAlphaScrollChangeListener
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (mOnAlphaScrollChangeListener != null) {
            mOnAlphaScrollChangeListener!!.onVerticalScrollChanged(t)
        }
    }

    interface OnAlphaScrollChangeListener {
        fun onVerticalScrollChanged(t: Int)
    }
}
