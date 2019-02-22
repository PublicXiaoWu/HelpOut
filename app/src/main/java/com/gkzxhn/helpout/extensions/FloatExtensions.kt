package com.gkzxhn.helpout.extensions

import android.util.TypedValue
import com.gkzxhn.helpout.common.App

/**
 * Created by 方 on 2018/3/6.
 */

fun Float.isPercentageNumber() = this >= 0f && this <= 1f

fun Float.dp2px(): Float {
    val r = App.mContext.resources
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, r.displayMetrics)
}