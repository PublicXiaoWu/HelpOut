package com.gkzxhn.helpout.customview

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.gkzxhn.helpout.extensions.dp2px

class RecyclerSpace : RecyclerView.ItemDecoration {

    private var space: Int = 0
    var color = -1
    private var mDivider: Drawable? = null
    private var mPaint: Paint? = null
    private var type: Int = 0

    private var leftRightMargin = 15f.dp2px().toInt()

    constructor(space: Int) {

        this.space = space

    }

    constructor(space: Int, color: Int) {

        this.space = space
        this.color = color
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.setColor(color)
        mPaint!!.setStyle(Paint.Style.FILL)
        mPaint!!.strokeWidth = (space * 2).toFloat()

    }

    constructor(space: Int, color: Int, type: Int) {

        this.space = space
        this.color = color
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.setColor(color)
        mPaint!!.setStyle(Paint.Style.FILL)
        mPaint!!.strokeWidth = (space * 2).toFloat()
        this.type = type

    }

    constructor(space: Int, mDivider: Drawable) {

        this.space = space
        this.mDivider = mDivider

    }

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {

        if (parent.getLayoutManager() != null) {

            if (parent.layoutManager is LinearLayoutManager && parent.layoutManager !is GridLayoutManager) {

                if ((parent.layoutManager as LinearLayoutManager).orientation == LinearLayoutManager.HORIZONTAL) {

                    outRect.set(space, 0, space, 0)

                } else {

                    outRect.set(0, space, 0, space)

                }

            } else {

                outRect.set(space, space, space, space)

            }

        }


    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        super.onDraw(c, parent, state)
        if (parent.getLayoutManager() != null) {

            if (parent.getLayoutManager() is LinearLayoutManager && parent.getLayoutManager() !is GridLayoutManager) {

                if ((parent.getLayoutManager() as LinearLayoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL) {

                    drawHorizontal(c, parent)

                } else {

                    drawVertical(c, parent)

                }

            } else {

                if (type == 0) {

                    drawGrideview(c, parent)

                } else {

                    drawGrideview1(c, parent)

                }

            }

        }

    }

    //绘制纵向 item 分割线

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {

        val top = parent.getPaddingTop()
        val bottom = parent.getMeasuredHeight() - parent.getPaddingBottom()
        val childSize = parent.getChildCount()
        for (i in 0 until childSize) {

            val child = parent.getChildAt(i)
            val layoutParams = child.getLayoutParams() as RecyclerView.LayoutParams
            val left = child.getRight() + layoutParams.rightMargin
            val right = left + space
            if (mDivider != null) {

                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(canvas)

            }
            if (mPaint != null) {

                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)

            }

        }

    }

    //绘制横向 item 分割线
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {

        val left = parent.getPaddingLeft()
        val right = parent.getMeasuredWidth() - parent.getPaddingRight()
        val childSize = parent.getChildCount()
        for (i in 0 until childSize) {

            val child = parent.getChildAt(i)
            val layoutParams = child.getLayoutParams() as RecyclerView.LayoutParams
            val top = child.getBottom() + layoutParams.bottomMargin
            val bottom = top + space
            if (mDivider != null) {

                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(canvas)

            }
            if (mPaint != null) {

                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)

            }


        }

    }

    //绘制grideview item 分割线 不是填充满的
    private fun drawGrideview(canvas: Canvas, parent: RecyclerView) {

        val linearLayoutManager = parent.getLayoutManager() as GridLayoutManager
        val childSize = parent.getChildCount()
        var other = (parent.getChildCount() - 1) / linearLayoutManager.spanCount
        if (other < 1) {

            other = 1

        }
        other = other * linearLayoutManager.getSpanCount()
        if (parent.getChildCount() < linearLayoutManager.getSpanCount()) {

            other = parent.getChildCount()

        }
        var top: Int
        var bottom: Int
        var left: Int
        var right: Int
        var spancount: Int
        spancount = linearLayoutManager.getSpanCount() - 1
        for (i in 0 until childSize) {

            val child = parent.getChildAt(i)
            val layoutParams = child.getLayoutParams() as RecyclerView.LayoutParams
            if (i < other) {

                var leftMargin: Int = 0
                var rightMargin: Int = 0
                if (i % linearLayoutManager.spanCount == 0) {
                    leftMargin = leftRightMargin
                }
                if ((i + 1) % linearLayoutManager.spanCount == 0) {
                    rightMargin = leftRightMargin
                }

                //划横线
                top = child.getBottom() + layoutParams.bottomMargin
                bottom = top + space * 2
                left = (layoutParams.leftMargin + child.measuredWidth) * (i % linearLayoutManager.spanCount) + leftMargin
                right = child.measuredWidth + left - leftMargin - rightMargin
                if (mDivider != null) {

                    mDivider!!.setBounds(left, top, right, bottom)
                    mDivider!!.draw(canvas)

                }
                if (mPaint != null) {

                    canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)

                }

            }
            if ((i + 1) % linearLayoutManager.spanCount != 0) {

                //画竖线
                top =  child.top + layoutParams.topMargin
                bottom = top + child.measuredHeight
                left = child.right
                right = left + space * 2
                if (mDivider != null) {

                    mDivider!!.setBounds(left, top, right, bottom)
                    mDivider!!.draw(canvas)

                }
                if (mPaint != null) {

                    canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)

                }

            }/* else {

                spancount += linearLayoutManager.spanCount

            }*/

        }

    }


    /** */

    private fun drawGrideview1(canvas: Canvas, parent: RecyclerView) {

        val linearLayoutManager = parent.getLayoutManager() as GridLayoutManager
        val childSize = parent.getChildCount()
        var top: Int
        var bottom: Int
        var left: Int
        var right: Int
        val spancount: Int
        spancount = linearLayoutManager.getSpanCount()
        for (i in 0 until childSize) {

            val child = parent.getChildAt(i)
            //画横线
            val layoutParams = child.getLayoutParams() as RecyclerView.LayoutParams
            top = child.getBottom() + layoutParams.bottomMargin
            bottom = top + space
            left = layoutParams.leftMargin + child.getPaddingLeft() + space
            right = child.getMeasuredWidth() * (i + 1) + left + space * i
            if (mDivider != null) {

                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(canvas)

            }
            if (mPaint != null) {

                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)

            }
            //画竖线
            top = (layoutParams.topMargin + space) * (i / linearLayoutManager.getSpanCount() + 1)
            bottom = (child.getMeasuredHeight() + space) * (i / linearLayoutManager.getSpanCount() + 1) + space
            left = child.getRight() + layoutParams.rightMargin
            right = left + space
            if (mDivider != null) {

                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(canvas)

            }
            if (mPaint != null) {

                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)

            }

            //画上缺失的线框
            if (i < spancount) {

                top = child.getTop() + layoutParams.topMargin
                bottom = top + space
                left = (layoutParams.leftMargin + space) * (i + 1)
                right = child.getMeasuredWidth() * (i + 1) + left + space * i
                if (mDivider != null) {

                    mDivider!!.setBounds(left, top, right, bottom)
                    mDivider!!.draw(canvas)

                }
                if (mPaint != null) {

                    canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)

                }

            }
            if (i % spancount == 0) {

                top = (layoutParams.topMargin + space) * (i / linearLayoutManager.getSpanCount() + 1)
                bottom = (child.getMeasuredHeight() + space) * (i / linearLayoutManager.getSpanCount() + 1) + space
                left = child.getLeft() + layoutParams.leftMargin
                right = left + space
                if (mDivider != null) {

                    mDivider!!.setBounds(left, top, right, bottom)
                    mDivider!!.draw(canvas)

                }
                if (mPaint != null) {

                    canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)

                }

            }

        }

    }
}