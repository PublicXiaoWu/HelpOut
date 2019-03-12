package com.gkzxhn.helpout.entity.UIInfo

/**
 * Created by 方 on 2018/5/7.
 */
data class NormalListItem(
        val text: String?,
        var isCheck: Boolean?,
        val type: Any? = null,
        val id: Long?,
        var subId: Long? = 0
)