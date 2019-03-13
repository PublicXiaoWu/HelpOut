package com.gkzxhn.helpout.entity

/**
 * Created by 方 on 2018/4/17.
 */


data class WXLawOrderInfo(
    var appId: String? = "",
    var merchantId: String? = "",
    var nonce: String? = "",
    var sign: String? = "",
    var prepayId: String? = "",
    var extension: String? = "",
    var timestamp: String? = ""
)