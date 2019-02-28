package com.gkzxhn.helpout.entity


data class CommentInfo(
    var rate: Int? = 0,
    var content: String? = "",
    var isResolved: Boolean? = false,
    var legalAdviceId: String? = "",
    var createdTime: String? = null
)