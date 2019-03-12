package com.gkzxhn.helpout.entity


data class FeedBackRequestInfo(
    var platform: String? = "ASSISTANT_APP",
    var problem: String? = "",
    var detail: String? = "",
    var attachments: List<String?>? = listOf()
)