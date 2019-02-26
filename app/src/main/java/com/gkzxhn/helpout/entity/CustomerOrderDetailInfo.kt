package com.gkzxhn.helpout.entity


data class CustomerOrderDetailInfo(
    var id: String? = "",
    var number: String? = "",
    var status: String? = "",
    var paymentStatus: String? = "",
    var category: String? = "",
    var lawyer: Lawyer? = Lawyer(),
    var reward: Double? = 0.0,
    var videoDuration: Int? = 0,
    var createdTime: String? = "",
    var acceptedTime: String? = "",
    var processedTime: String? = "",
    var endTime: String? = "",
    var isAutoEnd: Boolean? = false
)