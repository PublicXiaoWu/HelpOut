package com.gkzxhn.helpout.extensions

/**
 * Created by 方 on 2018/3/6.
 */

fun String.toColorHtml(colorStr: String) : String =
        "<font color=\"$colorStr\">${this}</font>"
