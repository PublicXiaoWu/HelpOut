package com.gkzxhn.helpout.view

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.gkzxhn.helpout.R


/**
 * 根据宽高比例自动计算高度ImageView
 */
class RatioImageView : android.support.v7.widget.AppCompatImageView {

    /**
     * 宽高比例
     */
    private var mRatio = 0f

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView)

        mRatio = typedArray.getFloat(R.styleable.RatioImageView_ratio, 0f)
        typedArray.recycle()
    }

    constructor(context: Context) : super(context) {}

    /**
     * 设置ImageView的宽高比
     *
     * @param ratio
     */
    fun setRatio(ratio: Float) {
        mRatio = ratio
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        if (mRatio != 0f) {
            val height = width / mRatio
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height.toInt(), View.MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val drawable = drawable
                drawable?.mutate()?.setColorFilter(Color.GRAY,
                        PorterDuff.Mode.MULTIPLY)
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                val drawableUp = drawable
                drawableUp?.mutate()?.clearColorFilter()
            }
        }

        return super.onTouchEvent(event)
    }

}
