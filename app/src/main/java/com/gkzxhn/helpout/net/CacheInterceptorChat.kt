package com.gkzxhn.helpout.net

import android.content.Context
import android.util.Log
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.utils.NetworkUtils
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response


/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/19 3:20 PM
 * @description：聊天后台相关
 */

class CacheInterceptorChat(val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain?): Response? {

        var request = chain?.request()
        if (NetworkUtils.isNetConneted(context)) {

            val addHeader = request?.newBuilder()

            val token = App.SP.getString(Constants.SP_TOKEN, "")
            if (token != null) {
                if (token.isNotEmpty()) {
                    val mtoken = "Bearer $token"
                    addHeader?.addHeader("Authorization", mtoken)
                }
            }

            val method = addHeader?.method(request!!.method(), request.body())

            return chain?.proceed(method?.build())

        } else {
            Log.e("CacheInterceptor", " no network load cahe")
            request = request?.newBuilder()?.cacheControl(CacheControl.FORCE_CACHE)?.build()
            val response = chain?.proceed(request)
            //set cahe times is 3 days
            val maxStale = 60 * 60 * 24 * 3
            return response?.newBuilder()?.removeHeader("Pragma")?.removeHeader("Cache-Control")?.header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")?.build()
        }
    }
}
