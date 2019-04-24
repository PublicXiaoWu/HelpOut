package com.gkzxhn.helpout.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.PopupWindow
import com.gkzxhn.helpout.R

object PopupWindowUtils {

    fun showBelow(context: Context) :PopupWindow {
        val contentView = LayoutInflater.from(context).inflate(R.layout.pop_window_msg_add, null, false)
        val pop = PopupWindow(contentView)
        return pop
    }
}