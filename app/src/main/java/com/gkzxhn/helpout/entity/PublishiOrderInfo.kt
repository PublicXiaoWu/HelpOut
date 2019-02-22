package com.gkzxhn.helpout.entity


data class PublishiOrderInfo(
    var id: String? = "",
    var number: String? = "",
    var status: String? = "",
    var paymentStatus: String? = "",
    var category: String? = "",
    var lawyer: Lawyer? = Lawyer(),
    var reward: Int? = 0,
    var videoDuration: Int? = 0,
    var createdTime: String? = "",
    var acceptedTime: String? = "",
    var processedTime: String? = "",
    var endTime: String? = "",
    var isAutoEnd: Boolean? = false
)

data class Lawyer(
    var id: String? = "",
    var name: String? = "",
    var username: String? = "",
    var rate: Int? = 0,
    var lawOffice: String? = ""
)