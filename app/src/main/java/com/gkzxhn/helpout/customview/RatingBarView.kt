package com.gkzxhn.helpout.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.extensions.dp2px


class RatingBarView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var mClickable = true
    private var onRatingListener: OnRatingListener? = null
    private var bindObject: Any? = null
    private var starImageSize: Float = 0.toFloat()
    private var starCount: Int = 0
    private var starEmptyDrawable: Drawable? = null
    private var starFillDrawable: Drawable? = null
    private var mStarCount: Int = 0

    interface OnRatingListener {
        fun onRating(bindObject: Any?, ratingScore: Int)
    }

    fun setBarClickable(clickable: Boolean) {
        this.mClickable = clickable
    }

    init {
        setOrientation(LinearLayout.HORIZONTAL)
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RatingBarView)
        starImageSize = ta.getDimension(R.styleable.RatingBarView_starImageSize, 20f)
        starCount = ta.getInteger(R.styleable.RatingBarView_starCount, 5)
        starEmptyDrawable = ta.getDrawable(R.styleable.RatingBarView_starEmpty)
        starFillDrawable = ta.getDrawable(R.styleable.RatingBarView_starFill)
        ta.recycle()

        for (i in 0 until starCount) {
            val imageView = getStarImageView(context, attrs)
            imageView.setOnClickListener {
                if (mClickable) {
                    mStarCount = indexOfChild(it) + 1
                    setStar(mStarCount, true)
                    if (onRatingListener != null) {
                        onRatingListener!!.onRating(bindObject, mStarCount)
                    }
                }
            }
            addView(imageView)
        }
    }

    private fun getStarImageView(context: Context, attrs: AttributeSet): ImageView {
        val imageView = ImageView(context)
        val para = LinearLayout.LayoutParams(Math.round(starImageSize), Math.round(starImageSize))
        para.marginStart = 2.5f.dp2px().toInt()
        para.marginEnd = 2.5f.dp2px().toInt()
        imageView.setLayoutParams(para)
        // TODO:you can change gap between two stars use the padding
        imageView.setPadding(0, 0, 0, 0)
        imageView.setImageDrawable(starEmptyDrawable)
//        imageView.setMaxWidth(10)
//        imageView.setMaxHeight(10)
        return imageView
    }

    fun setStar(starCount: Int, animation: Boolean) {
        var starCount = starCount
        starCount = if (starCount > this.starCount) this.starCount else starCount
        starCount = if (starCount < 0) 0 else starCount
        for (i in 0 until starCount) {
            (getChildAt(i) as ImageView).setImageDrawable(starFillDrawable)
            if (animation) {
                val sa = ScaleAnimation(0.7f, 0.7f, 1f, 1f)
                getChildAt(i).startAnimation(sa)
            }
        }
        for (i in this.starCount - 1 downTo starCount) {
            (getChildAt(i) as ImageView).setImageDrawable(starEmptyDrawable)
        }
    }

    fun getStarCount(): Int {
        return mStarCount
    }

    fun setStarFillDrawable(starFillDrawable: Drawable) {
        this.starFillDrawable = starFillDrawable
    }

    fun setStarEmptyDrawable(starEmptyDrawable: Drawable) {
        this.starEmptyDrawable = starEmptyDrawable
    }

    fun setStarCount(startCount: Int) {
        this.starCount = starCount
    }

    fun setStarImageSize(starImageSize: Float) {
        this.starImageSize = starImageSize
    }

    fun setBindObject(bindObject: Any) {
        this.bindObject = bindObject
    }

    fun setOnRatingListener(onRatingListener: OnRatingListener) {
        this.onRatingListener = onRatingListener
    }
}