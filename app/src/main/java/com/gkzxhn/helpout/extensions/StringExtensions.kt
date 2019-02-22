package com.gkzxhn.helpout.extensions

import android.text.TextUtils

/**
 * Created by 方 on 2018/3/6.
 */

fun String.toColorHtml(colorStr: String) : String =
        "<font color=\"$colorStr\">${this}</font>"

/**
 * 过滤emoji 或者 其他非文字类型的字符
 *
 * @param source
 * @return
 */
fun String.filterEmoji(): String {
    if (TextUtils.isEmpty(this)) {
        return this
    }
    var buf: StringBuilder? = null
    val len = this.length
    for (i in 0 until len) {
        val codePoint = this[i]
        if (isEmojiCharacter(codePoint)) {
            if (buf == null) {
                buf = StringBuilder(this.length)
            }
            buf.append(codePoint)
        }
    }
    if (buf == null) {
        return ""
    } else {
        if (buf.length == len) {
            buf = null
            return this
        } else {
            return buf.toString()
        }
    }
}
private fun isEmojiCharacter(codePoint: Char): Boolean {
    return (codePoint.toInt() == 0x0 || codePoint.toInt() == 0x9 || codePoint.toInt() == 0xA
            || codePoint.toInt() == 0xD
            || codePoint.toInt() >= 0x20 && codePoint.toInt() <= 0xD7FF
            || codePoint.toInt() >= 0xE000 && codePoint.toInt() <= 0xFFFD
            || codePoint.toInt() >= 0x10000 && codePoint.toInt() <= 0x10FFFF)
}