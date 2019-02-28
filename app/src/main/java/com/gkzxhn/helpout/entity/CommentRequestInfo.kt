package com.gkzxhn.helpout.entity


data class CommentRequestInfo(
    var rate: Int? = 0,
    var content: String? = "",
    var isResolved: Boolean? = false,
    var legalAdviceId: String? = ""
)