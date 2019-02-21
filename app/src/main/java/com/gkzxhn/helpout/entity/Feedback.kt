package com.gkzxhn.helpout.entity

data class Feedback(
        var id: Long? = 0,
        var content: String? = null,
        var contents: String? = null,
        var familyId: Any? = Any(),
        var createdAt: String? = "",
        var updatedAt: Any? = Any(),
        var name: String? = "",
        var phone: Any? = Any(),
        var uuid: Any? = Any(),
        var relationship: Any? = Any(),
        var jailId: Any? = Any(),
        var idCardFront: Any? = Any(),
        var idCardBack: Any? = Any(),
        var typeId: Int? = 0,
        var typeName: String? = "",
        var desc: String? = "",
        var imageUrls: String? = "",
        var isReply: Int? = 0,
        var reply: Any? = Any(),
        var replyAt: Any? = Any(),
        var answer: Any? = Any()
)