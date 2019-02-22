package com.gkzxhn.helpout.utils

import com.gkzxhn.helpout.extensions.toColorHtml

object HtmlTextUtils {

    /**
     * @param pairs 第一个是内容 第二个是颜色
     */
    fun getColorHtmlString(vararg pairs: Pair<String, String>) : String {
        val strHtml = StringBuffer()
        return pairs.fold(strHtml) {
            acc, pair ->
            acc.append(pair.first.toColorHtml(pair.second))
        }.toString()
    }
}