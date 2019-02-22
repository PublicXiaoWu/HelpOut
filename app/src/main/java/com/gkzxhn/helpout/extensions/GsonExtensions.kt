package com.gkzxhn.helpout.extensions

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody

/**
 * Created by 方 on 2018/3/27.
 */

fun <T> Gson.getRequestBody(t: T): RequestBody {
    return RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
            this.toJson(t))
}