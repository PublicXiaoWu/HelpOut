package com.gkzxhn.helpout.entity


data class FeedBackRequestInfo(
    var clientKey: String? = "assistant.app",
    var problem: String? = "",
    var detail: String? = "",
    var attachments: List<String?>? = listOf()
)