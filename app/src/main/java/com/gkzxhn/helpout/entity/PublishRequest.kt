package com.gkzxhn.helpout.entity


data class PublishRequest(
    var content: String? = "",
    var circleoffriendsPicture: List<CircleoffriendsPicture?>? = listOf()
)

data class CircleoffriendsPicture(
    var fileId: String? = ""
)